RM	=	rm -f

MAIN	=	todos





latex:
	latex $(MAIN).tex

dvi: latex
	xdvi $(MAIN).dvi

view: latex
	dvips $(MAIN).dvi -o $(MAIN).ps



clean:
	- $(RM) *.dvi *.toc *.log *.log *.aux *.ps