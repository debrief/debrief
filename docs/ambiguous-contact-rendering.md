# Rendering Ambiguous Sensor Contacts — JS/TS Implementation Guide

This document describes how Debrief renders ambiguous bearing lines for sensor contacts, extracted from `SensorContactWrapper.java`, and how to reimplement the logic in JavaScript/TypeScript.

## Overview

A sensor contact represents a single bearing observation from a sensor on a vessel. When the sensor cannot distinguish between two mirror-image bearings (port vs. starboard), the contact is **ambiguous** — it stores two bearing values and renders two lines from the same origin.

```
                    Primary bearing (base color)
                   /
                  /
  Origin --------*
                  \
                   \
                    Ambiguous bearing (darker color)
```

## Source Data Model

Each sensor contact needs these fields:

```typescript
interface SensorContact {
  /** Timestamp of the observation */
  dtg: Date;

  /** Bearing to target in degrees (0-360, clockwise from north) */
  bearing: number;

  /** Whether this contact has a valid bearing */
  hasBearing: boolean;

  /** Optional ambiguous bearing in degrees — NaN if not present */
  bearingAmbig: number;

  /** Whether to display the ambiguous bearing line */
  hasAmbiguousBearing: boolean;

  /** Optional range to target (e.g. in degrees of lat/lon). null = no range known */
  range: number | null;

  /** Explicit origin position, or null to derive from parent track */
  absoluteOrigin: LatLon | null;

  /** Display color (inherited from parent SensorWrapper if null) */
  color: string | null;

  /** Label text */
  label: string;

  /** Whether the label is visible */
  labelVisible: boolean;

  /** Where to draw the label: 'start' | 'middle' | 'end' */
  labelLocation: 'start' | 'middle' | 'end';

  /** Line style (solid, dashed, etc.) */
  lineStyle: number;

  /** Whether this contact is visible */
  visible: boolean;

  /** Back-reference to parent sensor name */
  sensorName: string;
}

interface LatLon {
  lat: number;  // degrees
  lon: number;  // degrees
}
```

## Step-by-Step Rendering Algorithm

### Step 1: Visibility Gate

```typescript
if (!contact.visible) return;
```

Also check that the parent track is visible at this contact's timestamp, if applicable.

### Step 2: Determine the Origin Point

The origin is where the bearing line starts — the sensor's location at the time of the observation.

```typescript
function getOrigin(contact: SensorContact, track: Track): LatLon | null {
  if (contact.absoluteOrigin) {
    // Contact has a fixed/absolute origin — use it directly
    return contact.absoluteOrigin;
  }

  // Otherwise, interpolate position from the parent track at this timestamp.
  // In Debrief, this also factors in array offset (towed array displacement).
  // For a basic implementation, find the track fix nearest to contact.dtg.
  return track.getPositionAt(contact.dtg);
}
```

If no origin can be resolved, skip rendering.

### Step 3: Check for Bearing Data

```typescript
if (!contact.hasBearing) return;
```

### Step 4: Detect Ambiguity

Both conditions must be true for the ambiguous line to render:

```typescript
const hasAmbig = !isNaN(contact.bearingAmbig) && contact.hasAmbiguousBearing;
```

The Java code checks both `!Double.isNaN(_bearingAmbig)` **and** `getHasAmbiguousBearing()`. The `_bearingAmbig` field can hold a numeric value even after ambiguity is "resolved" — the `_hasAmbiguous` flag is what controls display.

### Step 5: Calculate the Bearing Line Endpoint

Both lines (primary and ambiguous) use the same endpoint calculation. The key question: does the contact have a known range?

```typescript
/**
 * Maximum bearing line extent in degrees of latitude.
 * Prevents lines from wrapping around the globe.
 */
const MAXIMUM_SENSOR_BEARING_RANGE_DEGS = 5;

function calcEndpoint(
  origin: LatLon,
  bearingDegs: number,
  rangeDegs: number | null,
  viewBounds: { width: number; height: number }  // in degrees
): LatLon {

  let rangeToUse: number;

  if (rangeDegs != null) {
    // Use the known range directly (already in degrees)
    rangeToUse = rangeDegs;
  } else {
    // No range — extend line to fill the viewport.
    // Use twice the larger viewport dimension to guarantee
    // the line extends past the visible area.
    const twiceRange = 2 * Math.max(viewBounds.width, viewBounds.height);
    rangeToUse = Math.min(twiceRange, MAXIMUM_SENSOR_BEARING_RANGE_DEGS);
  }

  // Project the endpoint using flat-earth (short sailing) calculation
  return addVector(origin, bearingDegs, rangeToUse);
}
```

### Step 6: Flat-Earth Projection (Short Sailing)

This is the core coordinate math from Debrief's `FlatEarth.add()`. It uses the Admiralty Manual of Navigation "Short Sailing" algorithm:

```typescript
function addVector(
  origin: LatLon,
  bearingDegs: number,
  rangeDegs: number
): LatLon {
  if (rangeDegs === 0) return { ...origin };

  const latRad = degsToRads(origin.lat);
  const lonRad = degsToRads(origin.lon);
  const courseRad = degsToRads(bearingDegs);
  const distRad = degsToRads(rangeDegs);

  // 1. Departure (east-west displacement)
  const departure = distRad * Math.sin(courseRad);

  // 2. Delta latitude (north-south displacement)
  const deltaLat = distRad * Math.cos(courseRad);

  // 3. Mean latitude (for longitude scaling)
  const meanLat = latRad + deltaLat / 2.0;

  // 4. Delta longitude (corrected for latitude)
  const deltaLon = departure / Math.cos(meanLat);

  // 5. New position
  let newLat = radsToDegs(latRad + deltaLat);
  const newLon = radsToDegs(lonRad + deltaLon);

  // Clamp latitude to avoid polar singularity
  if (newLat > 89) newLat = 89;

  return { lat: newLat, lon: newLon };
}

function degsToRads(d: number): number {
  return d * Math.PI / 180.0;
}

function radsToDegs(r: number): number {
  return r * 180.0 / Math.PI;
}
```

### Step 7: Determine Colors

When the contact is ambiguous, the two bearing lines get different colors so the analyst can distinguish them. The port-side bearing gets the base color; the starboard-side bearing gets a darker variant.

```typescript
function getRelativeBearing(courseDeg: number, bearingDeg: number): number {
  let rel = bearingDeg - courseDeg;
  while (rel > 180) rel -= 360;
  while (rel < -180) rel += 360;
  return rel;
}

function isBearingToPort(
  contact: SensorContact,
  track: Track
): boolean {
  const fix = track.getNearestFixAt(contact.dtg);
  if (!fix) return false;
  const courseDeg = radsToDegs(fix.course); // track course in degs
  const rel = getRelativeBearing(courseDeg, contact.bearing);
  return rel < 0; // negative relative bearing = port side
}

function darkenColor(color: string, factor = 0.7): string {
  // Multiply each RGB channel by the factor.
  // CSS: you can also use color-mix() or HSL lightness adjustment.
  // Implementation depends on your rendering framework.
}
```

Color assignment logic:

```typescript
let bearingOneColor: string;
let bearingTwoColor: string | null;

if (hasAmbig) {
  const baseColor = getContactColor(contact);
  const darkColor = darkenColor(baseColor);

  if (isBearingToPort(contact, track)) {
    bearingOneColor = baseColor;    // primary bearing is port → base color
    bearingTwoColor = darkColor;    // ambiguous bearing is stbd → darker
  } else {
    bearingOneColor = darkColor;    // primary bearing is stbd → darker
    bearingTwoColor = baseColor;    // ambiguous bearing is port → base color
  }
} else {
  bearingOneColor = getContactColor(contact);
  bearingTwoColor = null;
}
```

**Rule**: Port bearing always gets the base (brighter) color. Starboard bearing always gets the darker color. This is independent of which bearing is "primary" vs "ambiguous."

### Step 8: Draw the Lines

```typescript
function renderContact(
  ctx: CanvasRenderingContext2D,  // or your rendering abstraction
  contact: SensorContact,
  track: Track,
  projection: Projection,        // world coords → screen coords
  viewBounds: { width: number; height: number }
): void {
  // --- origin ---
  const origin = getOrigin(contact, track);
  if (!origin) return;
  if (!contact.hasBearing) return;

  const hasAmbig = !isNaN(contact.bearingAmbig) && contact.hasAmbiguousBearing;
  const screenOrigin = projection.toScreen(origin);

  // --- primary bearing line ---
  const rangeDegs = contact.range; // null if unknown
  const farEnd = calcEndpoint(origin, contact.bearing, rangeDegs, viewBounds);
  const screenFarEnd = projection.toScreen(farEnd);

  // (compute colors as shown in Step 7)

  ctx.strokeStyle = bearingOneColor;
  ctx.beginPath();
  ctx.moveTo(screenOrigin.x, screenOrigin.y);
  ctx.lineTo(screenFarEnd.x, screenFarEnd.y);
  ctx.stroke();

  // --- ambiguous bearing line ---
  if (hasAmbig) {
    const ambigFarEnd = calcEndpoint(origin, contact.bearingAmbig, rangeDegs, viewBounds);
    const screenAmbigFarEnd = projection.toScreen(ambigFarEnd);

    ctx.strokeStyle = bearingTwoColor;
    ctx.beginPath();
    ctx.moveTo(screenOrigin.x, screenOrigin.y);
    ctx.lineTo(screenAmbigFarEnd.x, screenAmbigFarEnd.y);
    ctx.stroke();
  }

  // --- label ---
  if (contact.labelVisible) {
    let labelPos: LatLon;
    switch (contact.labelLocation) {
      case 'start':
        labelPos = origin;
        break;
      case 'end':
        labelPos = farEnd;
        break;
      case 'middle':
      default:
        labelPos = {
          lat: (origin.lat + farEnd.lat) / 2,
          lon: (origin.lon + farEnd.lon) / 2,
        };
        break;
    }
    const screenLabel = projection.toScreen(labelPos);
    ctx.fillStyle = getContactColor(contact);
    ctx.fillText(contact.label, screenLabel.x, screenLabel.y);
  }
}
```

### Step 9: Resolving Ambiguity (User Action)

Debrief provides "Keep port bearing" / "Keep starboard bearing" actions. The logic:

```typescript
function resolveAmbiguity(contact: SensorContact, keepPort: boolean, track: Track): void {
  const bearing1 = contact.bearing;
  const bearing2 = contact.bearingAmbig;
  const isPortBearing = isBearingToPort(contact, track);

  if (isPortBearing === keepPort) {
    // Primary bearing is on the side we want to keep — keep it as primary
    contact.bearing = bearing1;
    contact.bearingAmbig = bearing2;
  } else {
    // Swap: make the ambiguous bearing the primary one
    contact.bearing = bearing2;
    contact.bearingAmbig = bearing1;
  }

  // Mark ambiguity as resolved — only one line will render now
  contact.hasAmbiguousBearing = false;
}
```

After resolution, the ambiguous bearing value is still stored (enabling undo), but `hasAmbiguousBearing = false` prevents the second line from rendering.

## Hit Testing / Range Calculation

When checking if a mouse click is near a sensor contact, test distance to **both** lines:

```typescript
function distanceFromContact(
  contact: SensorContact,
  testPoint: LatLon,
  viewBounds: { width: number; height: number }
): number {
  const origin = getOrigin(contact, track);
  if (!origin) return Infinity;

  // Distance to primary bearing line (point-to-segment)
  const farEnd = calcEndpoint(origin, contact.bearing, contact.range, viewBounds);
  let minDist = pointToSegmentDistance(testPoint, origin, farEnd);

  // Also check ambiguous line
  if (!isNaN(contact.bearingAmbig) && contact.hasAmbiguousBearing) {
    const ambigEnd = calcEndpoint(origin, contact.bearingAmbig, contact.range, viewBounds);
    minDist = Math.min(minDist, pointToSegmentDistance(testPoint, origin, ambigEnd));
  }

  return minDist;
}
```

## Summary of Key Java Source Locations

| Concern | Java Source | Lines |
|---------|-----------|-------|
| Data fields | `SensorContactWrapper.java` | 545–599 |
| Paint method | `SensorContactWrapper.java` | 1202–1331 |
| Ambiguity color logic | `SensorContactWrapper.java` | 1249–1262 |
| Ambiguous line drawing | `SensorContactWrapper.java` | 1284–1298 |
| Endpoint calculation | `SensorContactWrapper.java` | 917–956 |
| Port/starboard check | `SensorContactWrapper.java` | 1151–1175 |
| Relative bearing math | `SensorContactWrapper.java` | 516–526 |
| Flat-earth projection | `FlatEarth.java` | 108–146 |
| Resolve ambiguity | `SensorContactWrapper.java` | 767–785 |
| Max bearing range constant | `SensorContactWrapper.java` | 500 |
