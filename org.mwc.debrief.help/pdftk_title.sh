cd ../org.mwc.cmap.combined.feature/root_installs
pdftk A=UserGuide_FlattenedForViewing.pdf B=DebriefNG.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNG.pdf
pdftk A=Tutorial_FlattenedForViewing.pdf B=DebriefTutorial.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefTutorial.pdf
