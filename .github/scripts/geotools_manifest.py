#!/usr/bin/env python3
"""Make org.mwc.cmap.geotools describe the GeoTools jars that are in it.

org.mwc.cmap.geotools.libs copies GeoTools and its dependencies into
org.mwc.cmap.geotools as version-free jars. This script rewrites, from the
jars present:

  - META-INF/MANIFEST.MF: Bundle-ClassPath (every jar) and Export-Package
    (every Java package in them, so plugins that require the bundle see
    GeoTools as before);
  - build.properties: bin.includes;
  - .classpath: the Eclipse IDE's library entries.

Run it after fetching the jars, when a GeoTools upgrade adds or drops a
dependency:

    mvn -pl org.mwc.cmap.geotools.libs generate-resources
    python3 .github/scripts/geotools_manifest.py

With --check it changes nothing and exits 1 if the files are out of date.
"""
import argparse
import pathlib
import re
import sys
import zipfile

BUNDLE = pathlib.Path(__file__).resolve().parents[2] / "org.mwc.cmap.geotools"
# the bundle's own jar, built by Lite's Ant build; not a library
OWN = {"cmap_geotools.jar"}
# packages kept private to the bundle: the Eclipse platform exports its own
# EMF, and a second export would clash in the RCP product
PRIVATE = ("org.eclipse.emf.",)


def jars():
    return sorted(p.name for p in BUNDLE.glob("*.jar") if p.name not in OWN)


def packages(names):
    res = set()
    for name in names:
        with zipfile.ZipFile(BUNDLE / name) as z:
            for entry in z.namelist():
                if not entry.endswith(".class") or entry.startswith("META-INF/"):
                    continue
                pkg = entry.rsplit("/", 1)[0] if "/" in entry else ""
                if pkg and "-" not in pkg and re.fullmatch(r"[\w/$]+", pkg):
                    res.add(pkg.replace("/", "."))
    return sorted(p for p in res if not p.startswith(PRIVATE))


def header(name, values):
    return name + ": " + ",\n ".join(values) + "\n"


def manifest(text, names, pkgs):
    lines = text.replace("\r\n", "\n").split("\n")
    out, skip = [], False
    for line in lines:
        if line.startswith("Bundle-ClassPath:") or line.startswith("Export-Package:"):
            skip = True
            continue
        if skip and line.startswith(" "):
            continue
        skip = False
        out.append(line)
    body = "\n".join(out).rstrip("\n") + "\n"
    return (body + header("Bundle-ClassPath", ["."] + names)
            + header("Export-Package", own_packages() + pkgs))


def own_packages():
    """the bundle's own packages, from its sources"""
    src = BUNDLE / "src"
    return sorted({str(f.parent.relative_to(src)).replace("/", ".") for f in src.rglob("*.java")})


def build_properties(text, names):
    start = text.index("bin.includes")
    end = start
    for m in re.finditer(r".*\n", text[start:]):
        end += m.end() - m.start()
        if not m.group(0).rstrip("\n").endswith("\\"):
            break
    entries = ["META-INF/", "."] + names
    block = "bin.includes = " + ",\\\n               ".join(entries) + "\n"
    return text[:start] + block + text[end:]


def classpath(text, names):
    kept = [l for l in text.split("\n") if 'kind="lib"' not in l]
    i = next(n for n, l in enumerate(kept) if "<classpath>" in l) + 1
    libs = ['\t<classpathentry exported="true" kind="lib" path="%s"/>' % n for n in names]
    return "\n".join(kept[:i] + libs + kept[i:])


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--check", action="store_true")
    args = ap.parse_args()
    names = jars()
    if not names:
        sys.exit("No jars in %s: run mvn -pl org.mwc.cmap.geotools.libs generate-resources"
                 % BUNDLE)
    pkgs = packages(names)
    stale = []
    for rel, fn in (("META-INF/MANIFEST.MF", lambda t: manifest(t, names, pkgs)),
                    ("build.properties", lambda t: build_properties(t, names)),
                    (".classpath", lambda t: classpath(t, names))):
        path = BUNDLE / rel
        old = path.read_text()
        new = fn(old)
        if new != old:
            stale.append(rel)
            if not args.check:
                path.write_text(new)
    if args.check and stale:
        sys.exit("Out of date: %s. Run .github/scripts/geotools_manifest.py" % ", ".join(stale))
    updated = "" if args.check else "; updated: " + (", ".join(stale) or "nothing")
    print("%d jars, %d packages%s" % (len(names), len(pkgs), updated))


if __name__ == "__main__":
    main()
