package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.router.Route;
import in.virit.wwcd.session.UISession;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

@MenuItem(title = "Read and write files", icon = VaadinIcon.FILE)
@Route(layout = MainLayout.class)
public class ReadAndWriteFilesView extends AbstractThing {

    public ReadAndWriteFilesView() {
        add(md("""
                We sure have been reading and writing files with uploads and downloads for decades. With the
                [File API](https://developer.mozilla.org/en-US/docs/Web/API/File_API) (and some related APIs) in the
                in the browser wecan do far more with then than to just send them to server. But with latest
                browsers we can also read just a part of the file, in the browser and even create files dynamically
                and save them to "virtual filesystem" in the web apps sandbox.
                
                 * Browser sandbox is there still, but you can request permissions, or get them via file
                   picker dialog (~ upload) or drag and drop.
                 * Example: [Slicing large files](https://github.com/viritin/flow-viritin/blob/2da18fb0907a4c753e3a8e73dc49acc7d3ee51ed/src/main/java/org/vaadin/firitin/components/upload/UploadFileHandler.java#L404) in the browser while uploading.
                   Bypasses max size limits in Vaadin/Spring Boot/Nginx and makes it possible to continue on a network hick-up without re-sending the whole file.
                 * Example: [mediainfo.js](https://mediainfo.js.org/demo/) can read metadata from large image, audio and video files - in the browser, before waiting for potentially several minutes of uploading to server.
                
                """));
    }
}