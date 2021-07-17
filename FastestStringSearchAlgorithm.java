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

		for (int i = 1; i < 2; i++)
		{
			new TestBed(i * 1024, 1000, 100)
					.withAlgorithms(
							bruteForce,
							bayerMoore,
							rabinKarp,
							knuthMorrisPratt,
							choudhary,
							my
					)
					.run();
		}
	}
}

