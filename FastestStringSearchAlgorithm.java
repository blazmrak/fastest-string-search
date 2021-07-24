import algorithms.*;
import console.Console;
import testbed.TestBed;
import testbed.TextSearch;

public class FastestStringSearchAlgorithm
{
	public static void main(String[] args)
	{
		Console c = new Console();
		c.createAndShowConsole();

		TextSearch bruteForce = new BruteForce();
		TextSearch bayerMoore = new BayerMoore();
		TextSearch rabinKarp = new RabinKarp();
		TextSearch knuthMorrisPratt = new KnuthMorrisPratt();
		TextSearch choudhary = new Choudhary();
		TextSearch my = new MyTextSearch();
		TextSearch updatedChoudhary = new UpdatedChoudhary();

		for (int i = 1; i < 100; i++)
		{
			new TestBed(i, 100, 100, false)
					.withAlgorithms(
							bruteForce,
							bayerMoore,
							rabinKarp,
							knuthMorrisPratt,
							choudhary,
							my,
							updatedChoudhary
					)
					.run();
		}
	}
}

