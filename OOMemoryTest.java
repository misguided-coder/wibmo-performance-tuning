import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OOMemoryTest {
	
	public static void main(String[] args) throws InterruptedException  {
		
		List<String> memoryEater = new ArrayList<>();
		
		for(int i=0;i<999999999;i++) {
			memoryEater.add("Memory is going to finish soon, save it - "+i);			
			TimeUnit.SECONDS.sleep(1);
		}
		
		System.out.println("Finish Border Line!!!!!");
		
	}

}
