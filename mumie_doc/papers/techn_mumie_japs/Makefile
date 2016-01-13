
techn_mumie_japs.dvi: techn_mumie_japs.tex
	latex techn_mumie_japs.tex
	latex techn_mumie_japs.tex

techn_mumie_japs.ps: techn_mumie_japs.dvi
	dvips techn_mumie_japs.dvi

techn_mumie_japs.pdf: techn_mumie_japs.dvi
	dvipdf techn_mumie_japs.dvi

.PHONY: dvi
dvi: techn_mumie_japs.dvi

.PHONY: ps
ps: techn_mumie_japs.ps

.PHONY: pdf
pdf: techn_mumie_japs.pdf

.PHONY: clean
clean:
	rm -vf *.log *.aux *.toc *.dvi *.pdf *.ps