import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class jobscheduler {

	public static void main(String[] args) throws IOException {
		String filePath = "C:\\Users\\chandni\\workspace2\\AlgoAssignment\\src\\input2.txt";
		if(args.length != 0){
			filePath = args[0];
		}
		File file = new File(filePath);
		File out_file = new File("output_file.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedWriter fw = new BufferedWriter(new FileWriter(out_file));
		String inp;
		Jobheap heap = new Jobheap(); 						//Object of JobHeap to keep jobs in min heap
		Rbtree rbtree = new Rbtree();						//Object of rbtree to keep jobs in red black tree
		ArrayList<String> al = new ArrayList<String>();		// string array to store parsed user commands " arrival time, commmand, job fields"
		int totalRuntime = 0;
		ArrayList<Integer> jobQueue = new ArrayList<Integer>();
		while((inp = br.readLine())!=null){
			//System.out.println(inp);
			String text[] = inp.split(" ");
			
			int time = (Integer.parseInt(text[0].substring(0, text[0].length()-1)));
			switch(command(text[1])){	//command method compares the input string values for (INSERT, PRINT,PRINT_RANGE,NEXT,PREVIOUS)
				case 1:
					//System.out.println("Insert");
					String[] value = getText(text[1]).split(" ");// getText to get integer inputs
					al.add(time + " insert " + value[0] + " " + value[1]); 	// value[0] = job id  value[1]= total time
					totalRuntime += Integer.parseInt(value[1]);				// totalRuntime keeps track of the total job time of all the inserted jobs

					break;
				case 2:
					//System.out.println("Print Single");
					value = getText(text[1]).split(" ");
					al.add(time + " single " + value[0]);		//value[0]=job id to print
					jobQueue.add(time);
					break;
				case 3:
					//System.out.println("Print Range");
					value = getText(text[1]).split(" ");
					al.add(time + " range " + value[0] + " " + value[1]);		//value[0]=starting job id  value[1]=ending job id
					jobQueue.add(time);
					break;
				case 4:
					//System.out.println("Next Job");
					value = getText(text[1]).split(" ");
					al.add(time + " next " + value[0]);		//value[0]=job id to print next
					jobQueue.add(time);
					break;
				case 5:
					//System.out.println("Previous Job");
					value = getText(text[1]).split(" ");
					al.add(time + " prev " + value[0]);		//value[0]=job id to print previous
					jobQueue.add(time);
					break;
				default:
					System.out.println("invalid instruction");
			}
		}
		Job previousJob = null;		// keeps track of the job currently executing
		boolean done = true;
		int previousTime = 0;
		int jobIndex = 0;
		boolean pendingJob = true;
		totalRuntime = 1000000;
		for(int systemTime=0; systemTime<totalRuntime;systemTime++){		//sytemTime variable is the global time counter , loop runs untill sytemTime is less than total time of all the inserted jobs
			if(previousJob != null){
				previousJob.executed_time++;
				if(previousJob.executed_time == previousJob.time_left){
					done = true;
					previousJob = null;		//no current job
				}
			}
			if (pendingJob){
				
				String commands[] = al.get(jobIndex).split(" ");		// commands[] array store parsed string from "al"
				if(Integer.parseInt(commands[0]) == systemTime){ 		//system time is equal to arrival time of job
					jobIndex+=1;
					if(jobIndex == al.size()){
						pendingJob = false;			// all the jobs in "al" have been executed
					}
					if(previousJob!=null){
						rbtree.insert(previousJob);
					}
					if(commands[1].equals("insert")){
						//System.out.println(commands[2] + " " + commands[3]);
						heap.insertKey(new Job(Integer.parseInt(commands[2]),Integer.parseInt(commands[3]),0));// execution_time=0
						rbtree.insert(new Job(Integer.parseInt(commands[2]),Integer.parseInt(commands[3]),0));// execution_time=0
					}
					else if(commands[1].equals("single")){
						Job job = rbtree.findUtil(Integer.parseInt(commands[2]));		// traverse the RB tree to find job of jobid=commands[2]
						//System.out.print(systemTime);
						System.out.println("Printing -> " + job.jobId + " " + job.executed_time + " " + job.time_left);
						String output = "(" + job.jobId + " ," + job.executed_time + ", " + job.time_left + ")";
						fw.write(output);
						fw.write("\n");
					}
					else if(commands[1].equals("range")){		// command to print jobs in the given range
						int st = Integer.parseInt(commands[2]);
						int ed = Integer.parseInt(commands[3]);
						//System.out.print(systemTime);
						//System.out.print(st + " " + ed);
						Rbtree.Node stNode = rbtree.nextNode(st,true);// gets the nextNode present in the range
						//System.out.println(stNode.key.jobId);
						String output = "";
						while(true){
							if(stNode.key.jobId == 0){
								break;
							}
							if(stNode.key.jobId > ed){
								break;
							}
							System.out.println("Range -> " + stNode.key.jobId + ", " + stNode.key.executed_time + ", " + stNode.key.time_left);
							//stNode = rbtree.nextNode(stNode);
							output += "(" + stNode.key.jobId + ", " + stNode.key.executed_time + ", " + stNode.key.time_left + ")";
							stNode = rbtree.nextNode(stNode);
							output += ",";
						}
						if(output == "")	output += "(" + "0" + " " + "0" + " " + "0" + "),";
						fw.write(output.substring(0, output.length()-1));
						fw.write("\n");
					}
					else if(commands[1].equals("next")){
						Job job = rbtree.next(Integer.parseInt(commands[2]));		// rbtree.next gets next job in rbtree
						//System.out.print(systemTime);
						System.out.println("Next -> " + job.jobId + " " + job.executed_time + " " + job.time_left);
						String output = "(" + job.jobId + ", " + job.executed_time + ", " + job.time_left + ")";
						fw.write(output);
						fw.write("\n");
					}
					else if(commands[1].equals("prev")){
						Job job = rbtree.prev(Integer.parseInt(commands[2]));		//gets previous job from rbtree
						//System.out.print(systemTime);
						System.out.println("Prev -> " + job.jobId + " " + job.executed_time + " " + job.time_left);
						String output = "(" + job.jobId + " ," + job.executed_time + " ," + job.time_left + ")";
						fw.write(output);
						fw.write("\n");
					}
					else{
						System.out.println("invalid");
					}
					if(previousJob!=null){
						rbtree.delete(previousJob);
					}
				}
			}
			
			if(systemTime-previousTime == 5 || done){		
				previousTime = systemTime;
				done = false;
				if(previousJob != null){
					if(previousJob.executed_time == previousJob.time_left){		// job completed 
						continue;
					}
					heap.insertKey(previousJob);		// the job is reinserted into the heap and rbtree after it is executed 
					rbtree.insert(previousJob);
				}
				Job job = heap.extractMin();	// extracts the job with minimum executed_time from heap for execution
				if(job.jobId == -1){
					continue;
				}
				previousJob = job;		
				rbtree.delete(job);		// deletes the job from rbtree for execution 
			}

			if(previousJob == null && heap.extractMin().jobId == -1)	done = true;
			
		}
		fw.close();
	}

/*function to split input string and store the integer input values of (JOb Id to insert, total time, range of job ids to print next, previous or the job with given id)
	 if two integer inputs like insert or print range commands then values seperated by " "*/

	private static String getText(String string) {
		String ret = "";
		boolean start = false;
		for(int i=0;i<string.length();i++){
			if(string.charAt(i)>='0' && string.charAt(i)<='9'){
				ret += string.charAt(i);
				start = true;
			}
			else if(start){
				ret += " "; 
			}
		}
		return ret.substring(0, ret.length()-1);
	}




	// function to read the input string and extract the command to be executed

	private static int command(String string) {		
		if(string.charAt(0) == 'I')		// Checks first character for "I" -> "INSERT command"
			return 1;
		else if(string.charAt(0) == 'N')	// Checks first character for "N" -> "NEXT command"
			return 4;
		else if(string.charAt(2) == 'e')	// Print and Previous both starts with 'P' so checks char at index=2 for "e" in "PREVIOUS command"
			return 5;
		else{
			for(int i=0;i<string.length();i++){ // Checks for "," in the string . "PRINT RANGE OF JOBS seperated by "," "
				if(string.charAt(i) == ',')
					return 3;
			}
			return 2;							// None of the above checks out then its a "PRINT command"
		}
	}

}
