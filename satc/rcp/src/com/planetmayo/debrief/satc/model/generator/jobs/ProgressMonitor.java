package com.planetmayo.debrief.satc.model.generator.jobs;

public interface ProgressMonitor
{
  /** 
   * Notifies that the main task is beginning. 	
   */
  void beginTask(String name, int totalWork);

  /** 
   * Notifies that the work is done; that is, either the main task is completed or the user canceled it.
   */  
  void done();
  
  /** 
   *  Returns whether cancelation of current operation has been requested.
   */  
  boolean isCanceled();
  
  /** 
   * Sets the cancel state to the given value.
   */  
  void setCanceled(boolean value);
  
  /**
   * checks is this job canceled and throws InterruptedException if yes
   */
  void checkCanceled() throws InterruptedException;

  /** 
   *  Sets the task name to the given value.
   */  
  void setTaskName(java.lang.String name);
  
  /** 
   * Notifies that a subtask of the main task is beginning.
   */  
  void subTask(java.lang.String name);
  
  /**
   * Notifies that a given number of work unit of the main task has been completed.
   */
  void worked(int work);
}
