input = read.table("input.csv", header=FALSE,sep=";") # read primary file

fileCount=nrow(input);
data = NULL;

# put all data together
for(i in 1:fileCount){
	curfile = toString(input[i,]$V6); # V6 is name of column containing input file
	partialData = read.table(curfile, header=FALSE);    # read the result file
	data = rbind(data, partialData);
}

# calculate statistics
n = nrow(data);
mean = mean(data);
sd = sd(data);
varCoef = mean / sd;
confidenceError = qnorm(0.995) * sd/sqrt(n);
left = mean - confidenceError;
right = mean + confidenceError;

zero = 0;

result = rbind( mean, right, left, varCoef, zero, zero, zero, zero, zero );

write.table(result, file="result", col.names=FALSE, row.names=FALSE);

q();
