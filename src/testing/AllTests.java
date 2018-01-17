package testing;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import memory.WorkingMemory;

@RunWith(Suite.class)
@SuiteClasses({
	DatabaseAccessorTest.class,
	MemoryTest.class,
	WorkingMemoryTest.class,
	ApplicantFilterTest.class,
	ControllerTest.class
})

public class AllTests{
	@BeforeClass
	public static void initData(){
		long start = System.nanoTime();
		System.out.println("Started loading data..");
		WorkingMemory.loadApplicantData();
		System.out.printf("Applicant data loaded in %2.3fs\n", (System.nanoTime() - start) * 1E-9);
	}//initData
}//AllTests
