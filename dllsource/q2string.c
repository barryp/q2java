/**
 * Functions for handling dynamically allocated strings, using the Quake2
 * memory management functions TagMalloc() and TagFree()
 */

#include <string.h>
#include "globals.h"
#include "q2string.h"


/**
 * Make a new string by concatenating 3 existing strings
 */
char *q2strcpy5(const char *source1, const char *source2, 
                const char *source3, const char *source4, 
                const char *source5)
	{
	char *result;
	int n1, n2, n3, n4, n5;

    // concatenating all nulls just returns a null
    if ((!source1) && (!source2) && (!source3) && (!source4) && (!source5))
        return 0;

    if (source1)
	    n1 = strlen(source1);
    else 
        n1 = 0;

    if (source2)
        n2 = strlen(source2);
    else
        n2 = 0;

    if (source3)
        n3 = strlen(source3);
    else
        n3 = 0;

    if (source4)
        n4 = strlen(source4);
    else
        n4 = 0;

    if (source5)
        n5 = strlen(source5);
    else
        n5 = 0;

    result = q2java_gi.TagMalloc(n1 + n2 + n3 + n4 + n5 + 1, TAG_GAME);
	if (result)
        {
        if (n1)
		    memcpy(result, source1, n1);
        if (n2)
            memcpy(result + n1, source2, n2);
        if (n3)
            memcpy(result + n1 + n2, source3, n3);
        if (n4)
            memcpy(result + n1 + n2 + n3, source4, n4);
        if (n5)
            memcpy(result + n1 + n2 + n3 + n4, source5, n5);

        result[n1 + n2 + n3 + n4 + n5] = 0;
        }

	return result;
	}


/**
 * Make a copy of an existing string
 *
 * @param source1 String to copy (may be null)
 * @param source2 String to copy (may be null)
 * @return copy of source1 + source2 (null if both source1 and source2 are null)
 */
char *q2strcpy4(const char *source1, const char *source2, const char *source3, const char *source4)
	{
	return q2strcpy5(source1, source2, source3, source4, 0);
	}

/**
 * Make a copy of an existing string
 *
 * @param source1 String to copy (may be null)
 * @param source2 String to copy (may be null)
 * @return copy of source1 + source2 (null if both source1 and source2 are null)
 */
char *q2strcpy3(const char *source1, const char *source2, const char *source3)
	{
	return q2strcpy5(source1, source2, source3, 0, 0);
	}



/**
 * Make a copy of an existing string
 *
 * @param source1 String to copy (may be null)
 * @param source2 String to copy (may be null)
 * @return copy of source1 + source2 (null if both source1 and source2 are null)
 */
char *q2strcpy2(const char *source1, const char *source2)
	{
	return q2strcpy5(source1, source2, 0, 0, 0);
	}


/**
 * Make a copy of an existing string
 *
 * @param source String to copy (may be null)
 * @return copy of source - null if source is null
 */
char *q2strcpy(const char *source)
	{
	return q2strcpy5(source, 0, 0, 0, 0);
	}


/**
 * Create a new string by appending source to dest, and 
 * free the old dest
 */
char *q2strcat(char *dest, const char *source)
    {
	char *result = q2strcpy3(dest, source, 0);
    if (dest)
        q2java_gi.TagFree(dest);
	return result;
    }


/** 
 * Allocate a string of a given length
 */
char *q2stralloc(int n)
    {
    char *result;

    // deal with negative values
    if (n < 1)
        return 0;

    result = q2java_gi.TagMalloc(n, TAG_GAME);
    *result = 0;
    return result;
    }


/**
 * Free a string
 */
void q2strfree(char *s)
    {
    // just a wrapper for Q2's TagFree
    if (s)
        q2java_gi.TagFree(s);
    }