input = read.table("input.csv", header=FALSE,sep=";") # read primary file
# add new columns in the desired order:
input$mean=0
input$sd=0

#process the secondary files (in 2nd input column):
i=1
num=nrow(input);
while(i<=num){
	curfile = toString(input[i,]$V2); # V2 is name of the second column
	fileData = scan(file=curfile);    # read the file into a vector
	input[i,]$mean=mean(fileData);
	input[i,]$sd=sd(fileData);
	rm(fileData);
	i=i+1;
}

# remove the file column from output
input$V2 <- NULL

# save the output & quit
write.table(input,file="output.csv", sep=";", col.names=FALSE, row.names=FALSE);
q();

################################################################################
##### Save to dataset: #########################################################
# dateTime:STRING
# mean:FLOAT
# sd:FLOAT
################################################################################
