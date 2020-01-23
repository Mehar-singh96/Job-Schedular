class Job{		
	int jobId;
	int time_left;
	int executed_time;
	
	Job(int jobId,int time_left,int executed_time){
		this.jobId = jobId;
		this.time_left = time_left;
		this.executed_time = executed_time;
	}
}

public class Jobheap {
	int capacity;
	int size;
	Job jobArray[];		//Array that stores value of the minheap
	
	public Jobheap(){
		this.capacity = 1;
		this.jobArray = new Job[1];
		this.size = 0;
	}
	
	
	private void MinHeapify(int idx){			// function to heapify job.Compare the executed time of job of the index with its left and right child's executed time and decidind the minimum node
		int l = (2*idx) + 1;
		int r = (2*idx) + 2;
		int smallest = idx;
		if(l<size && jobArray[l].executed_time < jobArray[idx].executed_time)
            smallest = l;
        if(r<size && jobArray[r].executed_time < jobArray[smallest].executed_time)
                smallest = r;
        if(smallest != idx){
        	//swap
            Job x = jobArray[idx];
            jobArray[idx] = jobArray[smallest];
            jobArray[smallest] = x;
        	
            MinHeapify(smallest);			//recursive moves through the heap to get the minimum element at the top
        }
	}
	
	public Job extractMin(){
        if(size<=0)
            return new Job(-1,99999999,0);
        if(size == 1){
            size-=1;
            return jobArray[0];
        }

        Job root = jobArray[0];
        jobArray[0] = jobArray[size - 1];  // take the last element from the heap and replace root with this.Then run Minheapify() to get the minimum at the root.
        size--;
        MinHeapify(0);
        return root;
    }
	
	private void decrese(int idx,int val){
       jobArray[idx].executed_time = val;
        int parentIdx = (idx-1)/2;
       
       // Decrease the executed time of the job and then compare the value with the parent's execution time.
       //while parent's execution time is greater than the node's exec time, swap the parent and child. 

        while(idx!=0 && (jobArray[parentIdx].executed_time > jobArray[idx].executed_time)){ 
            //swap
            Job x = jobArray[idx];
            jobArray[idx] = jobArray[parentIdx];
            jobArray[parentIdx] = x;
            idx = parentIdx;
        }
    }
	
	public void deleteKey(int idx){		//first decrease the execution time, then compare and swap with parents untill the node becomes the root node. Extract the node from the root with extractMin()
        decrese(idx,-99999999);
        extractMin();
    }
	
	private void increaseSize(){		// to increase the size of jobArray array . new capacity is twice the capacity of the old array. Copy the contents of the old array using newHeapArray[]
		int oldCapcity = this.capacity;
		this.capacity = this.capacity*2;
		
		Job newHeapArray[] = new Job[oldCapcity];
		
		for(int i=0;i<oldCapcity;i++){
			newHeapArray[i] = this.jobArray[i];		//copying job items from old jobArray to newHeapArray
		}
		
		this.jobArray = new Job[this.capacity];
		
		for(int i=0;i<oldCapcity;i++){
			this.jobArray[i] = newHeapArray[i];
		}
	}
	
	public void insertKey(Job job){
        if(size == capacity){		//if reached the full capacity increase size of array jobArray
            increaseSize();
        }
        jobArray[size] = job;
      
        size++;
        int idx = size - 1;
        int parentIdx = (idx-1)/2;
        while(idx!=0 && jobArray[parentIdx].executed_time > jobArray[idx].executed_time){		// comparing the values of the newly inserted node with the parent value and swapping untill parent.executed_time<child.executed_time
        	Job x = jobArray[idx];
        	jobArray[idx] = jobArray[parentIdx];
        	jobArray[parentIdx] = x;
        	
            idx = parentIdx;
            parentIdx = (idx-1)/2;
        }
    }
	
	public static void main(String[] args){
		Jobheap heap = new Jobheap();
		Job job1 = new Job(1,10,0);
		Job job2 = new Job(10,5,10);
		heap.insertKey(job1);
		heap.insertKey(job2);
		Job j = heap.extractMin();
		System.out.println(j.jobId + " " + j.time_left);
		j = heap.extractMin();
		System.out.println(j.jobId + " " + j.time_left);
	}
}
