import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.Kluster;
import org.apache.mahout.common..distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class KMeansCluster {

	public static void writePointsToFile(List<Vector> points, String fileName, FileSystem fs, Configuration conf) throws IOException { 
		Path path = new Path(fileName);
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, LongWritable.class, VectorWritable.class);
		long recNum = 0;
		VectorWritable vec = new VectorWritable();
		for (Vector point : points){
			vec.set(point);
			writer.append(new LongWritable(recNum++), vec);
		}
		writer.close();
	}

	public static List<Vector> getPoints(File file) throws IOException {
		List<Vector> points = new ArrayList<Vector>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		StringTokenizer st;
		double[] fr = new double[5];
		while ((line = br.readLine()) != null) {
			st = new StringTokenizer(line.replace(" ,", " "));
			while (st.hasMoreTokens()) {
				fr[0] = Double.parseDouble(st.nextToken());
				fr[1] = Double.parseDouble(st.nextToken());
				fr[2] = Double.parseDouble(st.nextToken());
				fr[3] = Double.parseDouble(st.nextToken());
				fr[4] = Double.parseDouble(st.nextToken());
				Vectir vec = new RandomAccessSparseVector(fr.length);
				vec.assign(fr);
				points.add(vec);
			}
		}
		br.close();
		return points;
	}

	
}
