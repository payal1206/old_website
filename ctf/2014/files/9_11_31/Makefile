.phony: all pdf clean

### CONFIG ###
SOURCES=$(wildcard *.adoc)
COLOUR=FF0000
##############

#rules
all:	$(SOURCES:.adoc=.html)
pdf:	$(SOURCES:.adoc=.pdf)
clean:
	@rm -f $(SOURCES:.adoc=.html) $(SOURCES:.adoc=.pdf) $(SOURCES:.adoc=.xml)

#creation
%.html: %.adoc
	@asciidoc -a numbered -a icons -a iconsdir=/etc/asciidoc/images/icons -a toclevels=4 -o $@ $<
#	highlight TODOs
	@sed -i 's/TODO/\<font color="#$(COLOUR)"\>\<b\>\<u\>TODO\<\/u\>\<\/b\>\<\/font\>/g' $@
#	collapse tables
	@sed -i 's/<p class=\"table\"><\/p>//g' $@

%.pdf: %.adoc
	@a2x -fpdf -darticle --icons --icons-dir=/etc/asciidoc/images/icons  --fop $<
