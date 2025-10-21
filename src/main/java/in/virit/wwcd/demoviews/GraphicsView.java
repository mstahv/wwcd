package in.virit.wwcd.demoviews;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import in.virit.TemperatureGauge;
import org.vaadin.firitin.appframework.MainLayout;
import org.vaadin.firitin.appframework.MenuItem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@MenuItem(title = "2D Graphics", icon = VaadinIcon.PAINTBRUSH)
@Route(layout = MainLayout.class)
public class GraphicsView extends AbstractThing {

    public GraphicsView() {
        add(md("""
                For 2D vector graphics we(b) can use two approaches:

                 * [Canvas](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API) - draw graphics with programmable API. Play [Tetris](https://tetris.demo.vaadin.com) (drawn with [Canvas](https://developer.mozilla.org/en-US/docs/Web/API/Canvas_API), via [Java API](https://vaadin.com/directory/component/canvas-java)).
                 * [SVG](https://developer.mozilla.org/en-US/docs/Web/SVG) - HTML but for vector graphics. DOM and styling support helps especially with dynamic and interactive content.

                 As Java developers, we can extend that with JDK's Graphics2D, with some tiny hacks.
                """));

        add(new H3("An SVG based Gauge widget (Java wrapper for a React component)"));
        add(new TemperatureGauge(22));

        add(new H3("Java Graphics2D rendering via raster image"));
        Graphics2DExample graphics2DExample = new Graphics2DExample();
        add(graphics2DExample);
        add(new Button("Add Oval", e -> graphics2DExample.drawOval()));

        add(new H3("A low level SVG component built in Vaadin's Element API (Vaadin 25, finally!)"));
        add(new MySVGComponent());

    }

    @Tag("svg")
    private class MySVGComponent extends Component implements HasSize {
        public MySVGComponent() {
            super(new Element("svg"));
            setWidth("100px");
            setHeight("100px");
            var rect = new Element("rect").setAttribute("x", "0")
                    .setAttribute("y", "0").setAttribute("width", "100")
                    .setAttribute("height", "100");
            var circle = new Element("circle").setAttribute("cx", "50")
                    .setAttribute("cy", "50").setAttribute("r", "40")
                    .setAttribute("fill", "white");
            circle.addEventListener("click", e -> {
                var current = circle.getAttribute("fill");
                if ("white".equals(current)) {
                    circle.setAttribute("fill", "red");
                } else {
                    circle.setAttribute("fill", "white");
                }
            });
            getElement().appendChild(rect, circle);
        }
    }

    public class Graphics2DExample extends Image {
        BufferedImage bufferedImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        public Graphics2DExample() {
            setWidth("200px");
            setHeight("200px");
            g2d.setStroke(new BasicStroke(5));
            drawRect();
        }

        public void drawOval() {
            // Draw a circle
            // Draw an oval
            g2d.setColor(Color.RED);
            g2d.drawOval(50, 50, 300, 300);
            refresh();
        }

        public void drawRect() {
            // Draw a rectangle
            g2d.setColor(Color.BLUE);
            g2d.drawRect(50, 50, 300, 300);
            refresh();
        }

        private void refresh() {
            setSrc((DownloadHandler) downloadEvent -> {
                downloadEvent.setContentType("image/png");
                try {
                    ImageIO.write(bufferedImage, "png", downloadEvent.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}