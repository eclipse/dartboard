package org.eclipse.dartboard.client;

public class AnalyzerStatusParams {

	private boolean isAnalyzing;

	public boolean isAnalyzing() {
		return isAnalyzing;
	}

	public void setAnalyzing(boolean isAnalyzing) {
		this.isAnalyzing = isAnalyzing;
	}

	@Override
	public String toString() {
		return AnalyzerStatusParams.class.getName() + " [isAnalyzing=" + isAnalyzing + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
