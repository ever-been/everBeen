# configuration
sleep.time <- 2
request.start <- "<start>"
request.end <- "<end>"
# END of configuration

# read the data file
data <- read.csv("data.csv", header=TRUE, sep=";")
# read the request rps sequence
rps <- unlist(read.csv("requests.csv", header=FALSE, sep=";"))

# prepare client data
clients <- unique(data$client)
nclients <- length(clients)

# get times of control statements
control.starts <- data[data$request==request.start,]
control.ends <- data[data$request==request.end,]

# add requested rps to control statement lists
for (i in 1:length(clients)) {
	client <- clients[[i]]
	control.starts$rps[control.starts$client==client] <- rps
	control.ends$rps[control.ends$client==client] <- -rps
}
control.statements <- merge(control.starts, control.ends, all=TRUE)

# compute the total requested RPS in each timestamp
rps.values <- list(time=c(), rps=c())
for (i in unique(control.statements$time)) {
	last.rps <- max(0, rps.values$rps[rps.values$time == max(0, rps.values$time)])
	delta.rps <- sum(control.statements$rps[control.statements$time == i])
	rps.values$time <- c(rps.values$time, i)
	rps.values$rps <- c(rps.values$rps, sum(last.rps, delta.rps))
}

# prepare data - number of requests per second (each client has a column)
data.rps.client <- table(data$time, data$client)
data.rps.served <- matrix(c(unique(data$time), rowSums(data.rps.client)), ncol=2)
rps.requested <- matrix(c(0, rps.values$time, max(data$time)+1, 0, rps.values$rps, 0), ncol=2)
rps.requested <- rps.requested[order(rps.requested[,1]),]

################################################################################
# setup graphing properties
nlines <- nclients+2  # number of lines in graph
xrange <- range(data$time)
yrange <- range(0,max(data.rps.served, unlist(rps.values$rps)))
colors <- rainbow(nlines)

# initialize plot
png("graph.png", width=600, height=300)
par(oma=c(0,0,0,0),  # outer margin - SWNE
    mar=c(2,3,2,0),  # margin - SWNE
	mgp=c(1.5,0.5,0),# space for axis annotation
	lty="solid",     # line type
	las=1,           # annotation direction 1=horizontal
	ann=FALSE,       # don't show axis title
	cex=0.75)        # font size (relative to default)
plot(xrange, yrange, type="n")
title("Served RPS In Time")

# draw the plot lines
for (i in 1:nclients) {
	lines(x=data.rps.client[,i], type="l", col=colors[i])
}
i <- nlines-1
lines(rps.requested, type="s", lwd=2, col=colors[i])  # type="h"
i <- nlines
lines(data.rps.served, type="l", lwd=2, col=colors[i])

# add title, subtitle and legend
legend(xrange[1], 0, clients, col=colors[1:nclients], bty="n", lty=par("lty"), lwd=3, title="Served Requests", yjust=0)
legend(xrange[2], 0, c("requested", "served"), col=colors[(nclients+1):nlines], bty="n", lty=par("lty"), lwd=3, title="Total Requests", xjust=1, yjust=0)
dev.off()
################################################################################
# additional computation and output for future refference

for (i in rps) {
	tmp.start <- max(control.starts$time[control.starts$rps==i]) + sleep.time
	tmp.end <- min(control.ends$time[control.ends$rps==-i]) - sleep.time
	tmp.data <- data[data$time>=tmp.start & data$time<=tmp.end,]
	tmp.data <- table(tmp.data$time)
	write(as.vector(tmp.data), file=paste(i,".csv", sep=""), sep=";")
}
