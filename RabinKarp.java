import java.math.BigInteger;
import java.util.Random;

public class RabinKarp implements TextSearch
{
	@Override
	public int search(
			char[] text,
			char[] pattern
	)
	{

		int patternSize = pattern.length; // m
		int textSize = text.length; // n

		long prime = (BigInteger.probablePrime(
				(Integer.SIZE - Integer.numberOfLeadingZeros(patternSize)) + 1,
				new Random()
		)).longValue();

		long r = 1;
		for (int i = 0; i < patternSize - 1; i++)
		{
			r *= 2;
			r = r % prime;
		}

		long[] t = new long[textSize];
		t[0] = 0;

		long pfinger = 0;

		for (int j = 0; j < patternSize; j++)
		{
			t[0] = (2 * t[0] + text[j]) % prime;
			pfinger = (2 * pfinger + pattern[j]) % prime;
		}

		int i;
		boolean passed;

		int diff = textSize - patternSize;
		for (i = 0; i <= diff; i++)
		{
			if (t[i] == pfinger)
			{
				passed = true;
				for (int k = 0; k < patternSize; k++)
				{
					if (text[i + k] != pattern[k])
					{
						passed = false;
						break;
					}
				}

				if (passed)
				{
					return i;
				}
			}

			if (i < diff)
			{
				long value = 2 * (t[i] - r * text[i]) + text[i + patternSize];
				t[i + 1] = ((value % prime) + prime) % prime;
			}
		}

		return -1;

	}

	@Override
	public String name()
	{
		return "Rabin Karp";
	}
}
