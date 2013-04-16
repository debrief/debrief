package com.planetmayo.debrief.satc.model.generator.jobs;

public interface ProgressMonitor
{
  void beginTask(String name, int totalWork);
  
  void done();
  
  void internalWorked(double work);
  
  boolean isCanceled();
  
  void setCanceled(boolean value);

  void setTaskName(java.lang.String name);
  
  void subTask(java.lang.String name);
  
  void worked(int work);
}
