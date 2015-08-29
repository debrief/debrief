cp DebriefNG_TMA_TutorialIndexed.pdf DebriefNGTutorialIndexed.pdf  ../org.mwc.cmap.combined.feature/root_installs
cd ../org.mwc.cmap.combined.feature/root_installs
pdftk A=../../org.mwc.debrief.help/front_covers/UserGuide_FlattenedForViewing.pdf B=DebriefNG.pdf cat A1 B2-end output out.pdf
mv out.pdf DebriefNG.pdf

pdftk A=../../org.mwc.debrief.help/front_covers/Tutorial_FlattenedForViewing.pdf B=DebriefNGTutorial.pdf cat A1 B2-end output out.pdf

#create DebriefNGTutorial bookmark
pdftk DebriefNGTutorialIndexed.pdf dump_data output report.txt
pdftk out.pdf update_info report.txt output outbook.pdf

mv outbook.pdf DebriefNGTutorial.pdf
rm out.pdf report.txt 

pdftk A=../../org.mwc.debrief.help/front_covers/TMATutorial_FlattenedForViewing.pdf B=DebriefNG_TMA_Tutorial.pdf cat A1 B2-end output out.pdf

#create DebriefNG_TMA_Tutorial bookmark
pdftk DebriefNG_TMA_Tutorial.pdf dump_data output report.txt
pdftk out.pdf update_info report.txt output outbook.pdf
mv outbook.pdf DebriefNG_TMA_Tutorial.pdf
rm out.pdf report.txt
