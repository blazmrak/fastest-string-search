package algorithms;

import testbed.TextSearch;

public class UpdatedChoudhary implements TextSearch
{
	private int ASIZE = 256;

	@Override
	public int search(
			char[] text,
			char[] pattern
	)
	{
		int i;
		int index;
		int end_index;
		boolean not_found;

		int text_len = text.length;
		int pattern_len = pattern.length;

		int pi_44 = pattern_len - 1;
		int pi_34 = (3 * pattern_len) / 4;
		int pi_24 = pattern_len / 2;
		int pi_14 = pattern_len / 4;

		int[] skip_table = new int[ASIZE];

		// preprocess pattern and fill skip_table
		for (i = 0; i < pattern_len; i++)
		{
			skip_table[pattern[i]] = pattern_len - 1 - i;
		}

		// now search
		for (i = 0; i < text_len; i++)
		{

			if ((text_len - i) < pattern_len)
			{
				return -1;
			}

			if (pattern[pi_44] != text[i + pi_44])
			{
				if (skip_table[text[i + pi_44]] > 0)
				{
					i = i + skip_table[text[i + pi_44]] - 1;
				}
				continue;
			}

			if (pattern[0] != text[i])
			{
				continue;
			}

			if (pattern[pi_24] != text[i + pi_24])
			{
				continue;
			}

			if (pattern[pi_34] != text[i + pi_34])
			{
				continue;
			}

			if (pattern[pi_14] != text[i + pi_14])
			{
				continue;
			}

			end_index = i + pi_44;
			not_found = false;

			for (index = i; index <= end_index; index++)
			{
				if (text[index] != pattern[index - i])
				{
					not_found = true;
					break;
				}
			} // end of inner for loop

			if (!not_found)
			{ // match is found
				return i;
			}

		} // end of outer for loop

		return -1;
	}


	@Override
	public String name()
	{
		return "Updated algorithms.Choudhary";
	}
}
