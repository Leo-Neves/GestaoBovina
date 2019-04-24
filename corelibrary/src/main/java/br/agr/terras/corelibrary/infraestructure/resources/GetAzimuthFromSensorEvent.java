package br.agr.terras.corelibrary.infraestructure.resources;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class GetAzimuthFromSensorEvent{
	private float azimuth = 0F;
	   
	 private float[] valuesAccelerometer = new float[3];
	 private float[] valuesMagneticField = new float[3];
	   
	 private float[] matrixR = new float[9];
	 private float[] matrixI = new float[9];
	 private float[] matrixValues = new float[3];
	 
	 
	
	public float getAzimuth(SensorEvent event){
		  
		   
		
		switch(event.sensor.getType()){
		  case Sensor.TYPE_ACCELEROMETER:
		   for(int i =0; i < 3; i++){
		    valuesAccelerometer[i] = event.values[i];
		   }
		   break;
		  case Sensor.TYPE_MAGNETIC_FIELD:
		   for(int i =0; i < 3; i++){
		    valuesMagneticField[i] = event.values[i];
		   }
		   break;
		  }
		    
		  boolean success = SensorManager.getRotationMatrix(
		       matrixR,
		       matrixI,
		       valuesAccelerometer,
		       valuesMagneticField); 
		    
		  if(success){
			  SensorManager.getOrientation(matrixR, matrixValues);
		     
		   
		     float azimuth = (float) Math.toDegrees(matrixValues[0]);
		     
		     if(isAXISZChanged(azimuth)){
					this.azimuth = azimuth;
			 }
		  }
		  
		  return this.azimuth;
	}
	
	private boolean isAXISZChanged(float azimuth){
		if(this.azimuth == azimuth){
			return false;			
		}else{
			return true;
		}	
	}
	
}