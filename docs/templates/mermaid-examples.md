# Mermaid Diagram Examples

Reference examples for LLM documentation diagrams. All render correctly in GitHub.

---

## Flowchart: Data Flow

Use for showing how data moves between components.

```mermaid
flowchart TD
    subgraph Input
        A[File Import] --> B[Parser]
        C[Manual Entry] --> B
    end

    B --> D{Validate}
    D -->|Valid| E[Domain Model]
    D -->|Invalid| F[Error Handler]

    E --> G[Store]
    E --> H[Display]
```

## Flowchart: Decision Logic

Use for showing algorithmic branching.

```mermaid
flowchart TD
    A[Start] --> B{Check Condition}
    B -->|Yes| C[Path A]
    B -->|No| D[Path B]
    C --> E{Another Check}
    E -->|True| F[Result 1]
    E -->|False| G[Result 2]
    D --> G
```

---

## Class Diagram: Inheritance

Use for showing class hierarchies.

```mermaid
classDiagram
    class Plottable {
        <<interface>>
        +getBounds() WorldArea
        +paint(CanvasType dest)
        +getName() String
    }

    class PlainWrapper {
        #_theName: String
        +getColor() Color
        +setVisible(boolean)
    }

    class TrackWrapper {
        -_thePositions: Collection
        +getPositions() Enumeration
        +addFix(FixWrapper)
    }

    class FixWrapper {
        -_theLocation: WorldLocation
        -_theTime: HiResDate
        +getLocation() WorldLocation
    }

    Plottable <|.. PlainWrapper
    PlainWrapper <|-- TrackWrapper
    PlainWrapper <|-- FixWrapper
    TrackWrapper o-- FixWrapper : contains
```

## Class Diagram: Composition

Use for showing has-a relationships.

```mermaid
classDiagram
    class WorldLocation {
        -_lat: double
        -_lon: double
        -_depth: double
        +getLat() double
        +getLong() double
    }

    class WorldVector {
        -_bearing: double
        -_range: WorldDistance
    }

    class WorldDistance {
        -_value: double
        -_units: int
        +getValueIn(int units) double
    }

    WorldVector *-- WorldDistance
    WorldLocation ..> WorldVector : creates
```

---

## Sequence Diagram: Method Calls

Use for showing runtime interactions.

```mermaid
sequenceDiagram
    participant User
    participant Editor
    participant TrackWrapper
    participant FixWrapper
    participant Canvas

    User->>Editor: openTrack()
    Editor->>TrackWrapper: getPositions()
    TrackWrapper-->>Editor: fixes[]

    loop For each fix
        Editor->>FixWrapper: getLocation()
        FixWrapper-->>Editor: WorldLocation
        Editor->>Canvas: drawPoint(location)
    end

    Canvas-->>User: display
```

## Sequence Diagram: File Parsing

Use for showing I/O flows.

```mermaid
sequenceDiagram
    participant File
    participant ImportReplay
    participant Parser
    participant TrackWrapper
    participant FixWrapper

    File->>ImportReplay: importFile(path)
    ImportReplay->>Parser: readLine()

    alt Track Definition
        Parser->>TrackWrapper: new TrackWrapper(name)
    else Fix Entry
        Parser->>FixWrapper: new FixWrapper(time, loc)
        Parser->>TrackWrapper: addFix(fix)
    else Comment
        Parser->>Parser: skip
    end

    ImportReplay-->>File: TrackWrapper[]
```

---

## Flowchart: Plugin Dependencies

Use for showing module relationships.

```mermaid
flowchart BT
    subgraph Core
        legacy[org.mwc.cmap.legacy]
        debrief_legacy[org.mwc.debrief.legacy]
    end

    subgraph Features
        core[org.mwc.debrief.core]
        plot[org.mwc.cmap.plotViewer]
        shift[org.mwc.debrief.track_shift]
    end

    debrief_legacy --> legacy
    core --> debrief_legacy
    plot --> legacy
    shift --> debrief_legacy
```

---

## Flowchart: Rendering Pipeline

Use for spatial rendering documentation.

```mermaid
flowchart LR
    subgraph World[World Space]
        WL[WorldLocation]
        WA[WorldArea]
    end

    subgraph Transform
        P[Projection]
    end

    subgraph Screen[Screen Space]
        SP[Screen Point]
        SR[Screen Rect]
    end

    subgraph Render
        C[Canvas]
        G[Graphics2D]
    end

    WL --> P
    WA --> P
    P --> SP
    P --> SR
    SP --> C
    SR --> C
    C --> G
```

---

## State Diagram: Track States

Use for showing state machines (if applicable).

```mermaid
stateDiagram-v2
    [*] --> Loading
    Loading --> Loaded: parse complete
    Loading --> Error: parse failed

    Loaded --> Editing: user modifies
    Editing --> Loaded: save
    Editing --> Loaded: cancel

    Loaded --> [*]: close
    Error --> [*]: dismiss
```

---

## Tips

1. **Keep diagrams focused** - One concept per diagram
2. **Use subgraphs** - Group related elements
3. **Label arrows** - Show what flows between nodes
4. **Use consistent naming** - Match class/method names in code
5. **Add notes for complexity** - Use `Note` elements for context
6. **Test in GitHub** - Preview to ensure rendering works
