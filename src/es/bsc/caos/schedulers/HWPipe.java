package es.bsc.caos.schedulers;

import java.util.ArrayList;
import java.util.List;

public class HWPipe {
	static int T2Capacity = 4;
	int capacity;
	ArrayList<Task> tasks;
	
	public HWPipe(int pipeCapacity) {
		capacity = pipeCapacity;
		tasks = new ArrayList<Task>();
	}
	
	public HWPipe(int pipeCapacity, List<Task> setTasks)
	{
		capacity = pipeCapacity;
		tasks = new ArrayList<Task>(setTasks);
	}

	/*
	 * creates an empty HWPipe with the UltraSPARC T2 capacity (4 tasks)
	 */
	public HWPipe()
	{
		this(HWPipe.T2Capacity);
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public boolean addTask(Task t)
	{
		if(tasks.size() < capacity)
		{
			tasks.add(t);
			return true;
		}
		return false;
	}
	
	/**
	 * This function add tasks from the provided list to
	 * the pipe. If there is not enough place for all tasks,
	 * N firsts tasks will be added. Where N is the number 
	 * of free places in the pipe.
	 * The function returns how many tasks are actually added.
	 * @param tsks list of tasks to add to the HW pipe
	 * @return number of tasks actually added
	 */
	public int addTasks(List<Task> tsks)
	{
		int cnt = 0;
		while(tasks.size() < capacity)
		{
			tasks.add(tsks.get(cnt++));
		}
		return cnt;
	}
	
	public Task getTask(int num)
	{
		if(tasks.size()>num)
			return tasks.get(num);
		else
			return null;
	}
	
	public List<Task> getTasks()
	{
		return tasks;
	}
	
}
