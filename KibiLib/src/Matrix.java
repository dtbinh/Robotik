


public class Matrix {
	private double[][] values;

	public Matrix(double[][] values) {
		super();
		this.values = values;
	}
	
	public int getN(){
		return values.length;
	}
	
	public int getM(){
		return values[0].length;
	}
	
	public double[] getRow(int row){
		double[] result = new double[getM()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values[row][i];
		}
		return result;
	}
	
	public double[] getColumn(int column){
		double[] result = new double[getN()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values[i][column];
		}
		return result;
	}
	
	public void addMultipleOfIToJ(int i, int j, double multiple){
		for (int k = 0; k < getM(); k++) {
			values[j][k]+=multiple*values[i][k];
		}
	}
	
	public double[] solve(double[] b){
		if(b.length!=getN())throw new RuntimeException("Wrong dimension");
		double[][] withB = new double[getN()][getM()+1];
		//copy this matrix
		for (int i = 0; i < getN(); i++) {
			for (int j = 0; j < getM(); j++) {
				withB[i][j] = values[i][j];
			}
		}
		//set the right site
		for (int i = 0; i < withB.length; i++) {
			withB[i][getM()]=b[i];
		}
		//create a new Matrix
		Matrix m = new Matrix(withB);
		//progress the gauss-algorithm
		for (int i = 0; i < m.getM()-1; i++) {
			for (int j = i+1; j < m.getN(); j++) {
				double multiple = -m.values[j][i]/m.values[i][i];
				m.addMultipleOfIToJ(i, j, multiple);
			}
		}
		//calculate the result
		double[] result = new double[getN()];
		for (int i = result.length-1; i>=0; i--) {
			result[i]=m.values[i][getM()];
			for (int j = i+1; j < result.length; j++) {
				result[i]-=m.values[i][j]*result[j];
			}
			result[i]/=m.values[i][i];
		}
		return result;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getN(); i++) {
			for (int j = 0; j < getM(); j++) {
				sb.append(values[i][j]+" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
