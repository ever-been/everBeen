package cz.cuni.mff.d3s.been.cluster.command;

/**
 * Author: donarus
 */
public final class DeleteTaskWrkDirCommand extends Command {

    private final String wrkDir;

    public DeleteTaskWrkDirCommand(final String taskWrkDir) {
        this.wrkDir = taskWrkDir;
    }

    public final String getTaskWrkDir() {
        return wrkDir;
    }

}
