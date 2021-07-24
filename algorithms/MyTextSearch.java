package algorithms;

import testbed.TextSearch;

import java.util.Arrays;

public class MyTextSearch implements TextSearch
{
	@Override
	public int search(
			char[] text,
			char[] pattern
	)
	{
		int[] skip_table = new int[256];
		int patternLength = pattern.length - 1;
		char lastPatternChar = pattern[patternLength];

		Arrays.fill(skip_table, patternLength);

		for (int i = 0; i < pattern.length; i++)
		{
			skip_table[pattern[i]] = patternLength - i - 1;
		}

		for (int i = 0; i < text.length; i++)
		{
			if (i + patternLength >= text.length)
			{
				return -1;
			}

			char lastTextChar = text[i + patternLength];
			if (lastPatternChar != lastTextChar)
			{
				i += skip_table[lastTextChar];
				continue;
			}

			if (isEqual(text, i, pattern))
			{
				return i;
			}
		}

		return -1;
	}

	private boolean isEqual(
			char[] text,
			int at,
			char[] pattern
	)
	{
		for (int j = 0; j < pattern.length; j++)
		{
			if (text[at + j] != pattern[j])
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String name()
	{
		return "My Text Search";
	}
}
