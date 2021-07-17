public class KnuthMorrisPratt implements TextSearch {
	@Override
	public int search(
			char[] text,
			char[] pattern
	)
	{
		int patternSize = pattern.length;
		int textSize = text.length;

		int i = 0, j = 0;

		int[] shift = KnuthMorrisPrattShift(pattern);

		while ((i + patternSize) <= textSize)
		{
			while (text[i + j] == pattern[j])
			{
				j += 1;
				if (j >= patternSize)
				{
					return i;
				}
			}

			if (j > 0)
			{
				i += shift[j - 1];
				j = (Math.max((j - shift[j - 1]), 0));
			}
			else
			{
				i++;
				j = 0;
			}
		}

		return -1;
	}

	private int[] KnuthMorrisPrattShift(char[] pattern)
	{

		int patternSize = pattern.length;

		int[] shift = new int[patternSize];
		shift[0] = 1;

		int i = 1, j = 0;

		while ((i + j) < patternSize)
		{
			if (pattern[i + j] == pattern[j])
			{
				shift[i + j] = i;
				j++;
			}
			else
			{
				if (j == 0)
				{
					shift[i] = i + 1;
				}

				if (j > 0)
				{
					i = i + shift[j - 1];
					j = (Math.max((j - shift[j - 1]), 0));
				}
				else
				{
					i = i + 1;
					j = 0;
				}
			}
		}

		return shift;

	}

	@Override
	public String name()
	{
		return "Knuth Morris Pratt";
	}
}
