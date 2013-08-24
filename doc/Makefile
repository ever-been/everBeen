# Documentation makefile
# WARNING: requires Pandoc, LaTeX with pdf convertors and xsltproc


targetDir=target
tocFile=src/book.xml
catTransFile=src/book-to-md.xsl
catFile=${targetDir}/catMd.sh
pdfFile=${targetDir}/doc.pdf

all:		pdf
targetdir:
		mkdir -p ${targetDir}

cat:		targetdir
		xsltproc ${catTransFile} ${tocFile} > ${catFile}
		chmod +x ${catFile}

pdf:		cat
		./${catFile} | pandoc  -f markdown+definition_lists+header_attributes -V geometry:margin=1in --chapters --toc --number-sections -o ${pdfFile}

clean:
		rm -f ${catFile}

veryclean:
		rm -rf ${targetDir}
