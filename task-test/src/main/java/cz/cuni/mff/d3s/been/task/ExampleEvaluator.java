package cz.cuni.mff.d3s.been.task;

import cz.cuni.mff.d3s.been.evaluators.EvaluatorResult;
import cz.cuni.mff.d3s.been.taskapi.Evaluator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

/**
 * @author Kuba Brecka
 */
public class ExampleEvaluator extends Evaluator {

	@Override
	public EvaluatorResult evaluate() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Linux", 29);
		dataset.setValue("Mac", 20);
		dataset.setValue("Windows", 51);

		JFreeChart chart = ChartFactory.createPieChart3D("Title", dataset, true, true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(290);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);

		BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setBackground(Color.white);
		graphics.clearRect(0, 0, 400, 400);

		chart.draw(graphics, new Rectangle2D.Double(0, 0, 400, 400));

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			File tmpFile = Files.createTempFile(null, null).toFile();
			ImageIO.write(image, "png", os);

			EvaluatorResult r = new EvaluatorResult();
			r.setTimestamp(new Date().getTime());
			r.setBenchmarkId(this.getBenchmarkId());
			r.setFilename("example-result.png");
			r.setMimeType(EvaluatorResult.MIME_TYPE_IMAGE_PNG);
			r.setData(os.toByteArray());
			return r;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
