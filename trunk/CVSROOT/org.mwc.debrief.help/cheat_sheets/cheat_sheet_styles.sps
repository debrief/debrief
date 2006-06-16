<?xml version="1.0" encoding="UTF-8"?>
<structure version="2" schemafile="contentFile.xsd" workingxmlfile="ViewingPlots\ChangingYourView.xml" templatexmlfile="">
	<nspair prefix="xsd" uri="http://www.w3.org/2001/XMLSchema"/>
	<template>
		<match overwrittenxslmatch="/"/>
		<children>
			<xpath allchildren="1"/>
		</children>
	</template>
	<template>
		<match match="description"/>
		<children>
			<xpath allchildren="1"/>
		</children>
	</template>
	<template>
		<match match="intro"/>
		<children>
			<xpath allchildren="1">
				<styles display="block" font-family="Arial"/>
			</xpath>
		</children>
	</template>
	<template>
		<match match="item"/>
		<children>
			<paragraph paragraphtag="p">
				<children>
					<template>
						<match match="@title"/>
						<children>
							<xpath allchildren="1">
								<styles font-weight="bold"/>
							</xpath>
						</children>
					</template>
					<text fixtext="."/>
					<template>
						<match match="description"/>
						<children>
							<xpath allchildren="1">
								<styles padding-left="2px"/>
							</xpath>
						</children>
					</template>
				</children>
			</paragraph>
		</children>
	</template>
	<pagelayout>
		<properties pagemultiplepages="0" pagenumberingformat="1" pagenumberingstartat="1" paperheight="11in" papermarginbottom="0.79in" papermarginleft="0.6in" papermarginright="0.6in" papermargintop="0.79in" paperwidth="8.5in"/>
	</pagelayout>
</structure>
