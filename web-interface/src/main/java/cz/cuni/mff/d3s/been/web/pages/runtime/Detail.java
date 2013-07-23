package cz.cuni.mff.d3s.been.web.pages.runtime;

import cz.cuni.mff.d3s.been.api.CommandTimeoutException;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntry;
import cz.cuni.mff.d3s.been.core.protocol.command.CommandEntryState;
import cz.cuni.mff.d3s.been.core.ri.*;
import cz.cuni.mff.d3s.been.web.components.Layout;
import cz.cuni.mff.d3s.been.web.model.TaskWrkDirChecker;
import cz.cuni.mff.d3s.been.web.pages.Page;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * This page is used to display information about host runtime and to resolve problems
 * on this host runtime
 *
 * @author Kuba Brecka, donarus
 */
@Page.Navigation(section = Layout.Section.RUNTIME_DETAIL)
public class Detail extends Page {

    // -----------------
    // INJECTED SERVICES
    // -----------------

    /**
     * Used to update zones returned from ajax link callbacks.
     */
    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @Environmental
    private JavaScriptSupport javaScriptSupport;


    // --------------
    // OTHER SERVICES
    // --------------

    /**
     * Initialized in onActivate method
     */
    @Property
    private TaskWrkDirChecker taskWrkDirChecker;


    // ---------------
    // LOOP PROPERTIES
    // ---------------
    /**
     * property for loop through CPUs
     */
    @Property
    private Cpu cpu;

    /**
     * property for loop through network interfaces
     */
    @Property
    private NetworkInterface networkInterface;

    /**
     * property for loop through file systems
     */
    @Property
    private Filesystem filesystem;

    /**
     * property for loop through all (used or unused task directories)
     */
    @Property
    private String taskDir;

    /**
     * property for loop through all unused task directories (from failed tasks)
     */
    @Property
    private String oldTaskWrkDir;

    /**
     * property for loop through interfaces of sample monitor
     */
    @Property
    private NetworkSample monitorInterface;

    /**
     * property for loop through file systems of monitor sample
     */
    @Property
    private FilesystemSample monitorFilesystem;

    /**
     * index property for loop through old task working directories (used to identify correct zone which
     * will be returned from "delete old task working directory" ajax link callback.
     */
    @Property
    private int zoneIdIndex;


    // ----------------
    // RENDERED OBJECTS
    // ----------------

    /**
     * displayed runtime
     */
    @Property
    private RuntimeInfo runtime;


    // --------------------
    // BLOCKS FOR INJECTION
    // --------------------

    /**
     * "Old task working directory has been deleted" block
     */
    @Inject
    private Block deletedBlock;

    /**
     * "Old task working directory deletion has timeouted" block
     */
    @Inject
    private Block timeoutedBlock;

    /**
     * "Old task working directory deletion has failed" block
     */
    @Inject
    private Block errorBlock;


    // ------
    // ASSETS
    // ------

    @Inject
    @Path("context:img/ajax-loader.gif")
    private Asset spinner;


    // -------------------
    // PAGE INITIALIZATION
    // -------------------

    /**
     * Initialize displayed runtime. See official tapestry documentation for more info
     * about conventions and page initialization.
     *
     * @param runtimeId id of displayed runtime
     */
    void onActivate(String runtimeId) {
        runtime = api.getApi().getRuntime(runtimeId);
        taskWrkDirChecker = new TaskWrkDirChecker(api.getApi());
    }

    /**
     * Return value will be added to all links to this page rendered on this page. See official
     * tapestry documentation for more info about conventions.
     *
     * @returm id of displayed runtime
     */
    Object onPassivate() {
        return runtime.getId();
    }

    /**
     * Prepare page for rendering - add loading spinner to all links with html class attribute ".progress_on_click"
     */
    public void setupRender() {
        super.setupRender();
        javaScriptSupport.addScript(
                "        $(document).on(Tapestry.TRIGGER_ZONE_UPDATE_EVENT, '.progress_on_click', function() {\n" +
                        "            $(this).after('<img src=\\\"" + spinner.toClientURL() + "\\\"/>');\n" +
                        "            jQuery(this).hide();\n" +
                        "        });");
    }


    // -------------------
    // AJAX LINK CALLBACKS
    // -------------------

    /**
     * Callback for "delete old task working directory" ajax link.
     * <br/><br/>
     * timeout: 30s
     * <br/> Updates correct zone through {@link AjaxResponseRenderer} with correct block ({@link Detail#deletedBlock}
     * if directory has been deleted, {@link Detail#errorBlock} if deletion has failed and {@link Detail#timeoutedBlock}
     * if deletion has timeouted)
     *
     * @param oldTaskWorkingDir task directory which should be deleted
     * @param zoneIndex         index of the zone which should be updated after this callback
     */
    public void onActionFromDeleteOldTaskWrkDir(String oldTaskWorkingDir, int zoneIndex) {
        this.oldTaskWrkDir = oldTaskWorkingDir;
        // create request for delete operation
        try {
            CommandEntry entry = api.getApi().deleteTaskWrkDirectory(runtime.getId(), oldTaskWorkingDir);

            if (entry.getState() == CommandEntryState.FAILED) {
                ajaxResponseRenderer.addRender("oldTaskWrkDirDeleteZone_" + zoneIndex, errorBlock);
            } else {
                ajaxResponseRenderer.addRender("oldTaskWrkDirDeleteZone_" + zoneIndex, deletedBlock);
            }
        } catch (CommandTimeoutException e) {
            ajaxResponseRenderer.addRender("oldTaskWrkDirDeleteZone_" + zoneIndex, timeoutedBlock);
        }

    }

    /**
     * Delete ajax link callback from delete link in {@link Detail#errorBlock}
     * see {@link Detail#onActionFromDeleteOldTaskWrkDir(String, int)}
     *
     * @param oldTaskWorkingDir task directory which should be deleted
     * @param zoneIndex         index of the zone which should be updated after this callback
     */
    public void onActionFromDeleteOldTaskWrkDir_failed(String oldTaskWorkingDir, int zoneIndex) {
        onActionFromDeleteOldTaskWrkDir(oldTaskWorkingDir, zoneIndex);
    }

    /**
     * Delete ajax link callback from delete link in {@link Detail#timeoutedBlock}
     * see {@link Detail#onActionFromDeleteOldTaskWrkDir(String, int)}
     *
     * @param oldTaskWorkingDir task directory which should be deleted
     * @param zoneIndex         index of the zone which should be updated after this callback
     */
    public void onActionFromDeleteOldTaskWrkDir_timeouted(String oldTaskWorkingDir, int zoneIndex) {
        onActionFromDeleteOldTaskWrkDir(oldTaskWorkingDir, zoneIndex);
    }

}
