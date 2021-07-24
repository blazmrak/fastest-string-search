package algorithms;

import testbed.TextSearch;

public class BayerMoore implements TextSearch
{
	int ASIZE = 256;

	@Override
	public int search(
			char[] text,
			char[] pattern
	)
	{
		int i;
		int j;

		int m = pattern.length;
		int n = text.length;

		int[] bmGs = new int[m];
		int[] bmBc = new int[ASIZE];

		preBmGs(pattern, m, bmGs);
		preBmBc(pattern, m, bmBc);

		j = 0;
		while (j <= n - m)
		{
			i = m - 1;
			while ((i >= 0) && (pattern[i] == text[i + j]))
			{
				--i;
			}
			if (i < 0)
			{
				return j;
			}
			else
			{
				j += (Math.max(bmGs[i], (bmBc[text[i + j]] - m + 1 + i)));
			}
		}

		return -1;
	}

	@Override
	public String name()
	{
		return "Bayer Moore";
	}

	private void preBmBc(
			char[] pattern,
			int m,
			int[] bmBc
	)
	{

		int i;

		for (i = 0; i < ASIZE; ++i)
		{
			bmBc[i] = m;
		}

		for (i = 0; i < m - 1; ++i)
		{
			bmBc[pattern[i]] = m - i - 1;
		}
	}

	private void preBmGs(
			char[] pattern,
			int m,
			int[] bmGs
	)
	{

		int i;
		int j;

		int[] suff = new int[m];

		suffixes(pattern, m, suff);

		for (i = 0; i < m; ++i)
		{
			bmGs[i] = m;
		}

		j = 0;

		for (i = m - 1; i >= 0; --i)
		{
			if (suff[i] == i + 1)
			{
				for (; j < m - 1 - i; ++j)
				{
					if (bmGs[j] == m)
					{
						bmGs[j] = m - 1 - i;
					}
				}
			}
		}

		for (i = 0; i <= m - 2; ++i)
		{
			bmGs[m - 1 - suff[i]] = m - 1 - i;
		}

	}

	private void suffixes(
			char[] pattern,
			int m,
			int[] suff
	)
	{

		int f = 0;
		int g;
		int i;

		suff[m - 1] = m;
		g = m - 1;

		for (i = m - 2; i >= 0; --i)
		{
			if (i > g && suff[i + m - 1 - f] < i - g)
			{
				suff[i] = suff[i + m - 1 - f];
			}
			else
			{
				if (i < g)
				{
					g = i;
				}
				f = i;
				while (g >= 0 && pattern[g] == pattern[g + m - 1 - f])
				{
					--g;
				}
				suff[i] = f - g;
			}
		}

	}
}
