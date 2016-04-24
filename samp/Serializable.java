package edu.mit.ai.stauffer;

import java.io.*;
import java.awt.Container;
import java.awt.Image;
import java.awt.image.*;
/**
 * This is a two dimensional co-occurrence matrix.
 */
public class CoMatrix implements Serializable {
  final float epsilon=(float)1e-20;
  DataSet dataSets[]=new DataSet[2];
  float matrix[];
  double matrixSum=0;

  /**
   * constructor #1.
   */
  public CoMatrix(DataSet tempDataSet) {
    super();
    dataSets[0]=tempDataSet;
    dataSets[1]=null;
    setSize();
  }

  /**
   * constructor #2.
   * @see java.util.Vector#setSize
   */
  public CoMatrix(DataSet dataSet0, DataSet dataSet1) {
    super();
    dataSets[0]=dataSet0;
    dataSets[1]=dataSet1;
    setSize();
  }

  public CoMatrix() {
    this(new DataSet(0));
  }

  DataSet dataSet(int pos) {
    if (symetric())
      return dataSets[0];
    else
      return dataSets[pos];
  }

  DataSet dataSet() {
    return dataSet(0);
  }

  void setMatrixSum(double tempSum) {
    matrixSum=tempSum;
  }

  void computeMatrixSum() {
    double tempSum=0.0;
    for (int x=matrix.length-1;x>=0;x--)
      tempSum+=matrix[x];
    setMatrixSum(tempSum);
  }

  public void resetMatrix(float value) {
    for (int x=matrix.length-1;x>=0;x--)
      matrix[x]=value;
    setMatrixSum(matrix.length*value);
  }

  public void resetMatrix() {
    resetMatrix(0);
    setMatrixSum(0);
  }

  public void save(String filename) {
    try {
      System.out.println("writing:"+this);
      ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(filename));
      out.writeObject(this);
      out.flush();
      out.close();
    } catch (Exception e) {System.out.println("save(filename) Exception:"+e);}
    //try {System.in.read(new byte[5],0,1);} catch (Exception e) {}
  }

  public void load(String filename) {
    try {
      System.out.print(" read:");
      ObjectInputStream in=new ObjectInputStream(new FileInputStream(filename));
      this.copy((CoMatrix)in.readObject());
      computeMatrixSum();
    } catch (Exception e) {System.out.println("load(filename) Exception:"+e);}
    //try {System.in.read(new byte[5],0,1);} catch (Exception e) {}
  }

  public void copy(CoMatrix tempCoMat) {
    this.dataSets[0]=tempCoMat.dataSets[0];
    this.dataSets[1]=tempCoMat.dataSets[1];
    this.matrix=tempCoMat.matrix;
    computeMatrixSum();
  }

  /**  This procedure accumulates the co-occurrence resulting from the cumScores
   *   in the two underlying datasets.  It assumes that each score accumulates a
   *   total of 1.0 to the cumScores vector.
   */
  public void accumCumScores() {
    DataSet ds0=dataSet(0);
    DataSet ds1=dataSet(1);
    float ds0Sum=(float)0.0;
    for (int x=0; x<ds0.maxCapacity; x++)
      ds0Sum+=ds0.cumScores[x];
    float normFactor=(float)1/(ds0Sum*(ds0Sum-1)+epsilon);//(float)1.0;
    //System.out.println("ds0sum:"+ds0Sum+",normFactor:"+normFactor);
    //float ds1Sum=(float)0.0;
    //for (int x=0; x<ds0.maxCapacity; x++)
    //  ds0Sum+=ds0.cumScores[x];
    int pos=ds0.maxCapacity*ds1.maxCapacity-1;
    for (int y=ds1.maxCapacity-1;y>=0;y--) {
      float yval=ds0.cumScores[y];
      if (yval!=0) {
        for (int x=ds0.maxCapacity-1;x>=0;x--) {
          matrix[pos]+=ds0.cumScores[x]*ds1.cumScores[y]*normFactor;
          matrixSum+=matrix[pos--];
        }
      } else {
        pos-=ds0.maxCapacity;
      }
    }
    if (symetric()) { // don't count hits on autocorrelation
      for (int x=ds0.maxCapacity-1;x>=0;x--) {
        pos=ds0.maxCapacity*x+x;
        matrix[pos]-=ds0.cumScores[x]*normFactor;
        matrixSum-=ds0.cumScores[x]*normFactor;
      }
    }
  }

  public void accumScores() {
    DataSet ds0=dataSet(0);
    DataSet ds1=dataSet(1);
    int pos=ds0.maxCapacity*ds1.maxCapacity-1;
    for (int y=ds1.maxCapacity-1;y>=0;y--) {
      float yval=ds0.cumScores[y];
      if (yval==0) {
        for (int x=ds0.maxCapacity-1;x>=0;x--) {
          matrix[pos]+=ds0.scores[x]*ds1.scores[y];
          matrixSum+=matrix[pos--];
        }
      } else {
        pos-=ds0.maxCapacity;
      }
    }
  }

  public boolean symetric() {
    return (dataSets[1]==null);
  }

  private int getSize() {
    return dataSet(0).maxCapacity*dataSet(1).maxCapacity;
  }

  void setSize() {
    if ((matrix==null)||(matrix.length!=(getSize()))) {
      matrix=new float[getSize()];
      computeMatrixSum();
    }
  }

  public void paintOn(Container container, Container glblcontainer) {
    //System.out.println("my new coMat.paintOn(xxx);");
    Image tempImage;
    container.getGraphics().fillRect(0,0,container.getBounds().width,container.getBounds().height);
    int width=dataSet(0).maxCapacity;
    int height=dataSet(1).maxCapacity;
    int[] pixels=new int[width*height];
    float maxValue=(float)1e-10;
    for (int xx=0;xx<width*height;xx++) {
      if (matrix[xx]>maxValue)
        maxValue=matrix[xx];
    }
    for (int xx=0;xx<width*height;xx++) {
      if (xx<matrix.length) {
        int value=(int)((matrix[xx]/maxValue)*255);
        pixels[xx]=(0xff000000)|(value<<16)|(value<<8)|(value);
      } else
        pixels[xx]=(0xff000000);
      }
    tempImage=java.awt.Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width,height,pixels,0,width));
    container.getGraphics().drawImage(tempImage,0,0,container.getBounds().width,container.getBounds().height,glblcontainer);
  }

}
