/**
 * Read Java-style property files - blank lines 
 * are ignored, comment lines begin with '#'.
 *
 * Lines that match "q2java_*=*" are handled specially with a call
 * to javalink_property(char *name, char *value).
 *
 * Lines matching "include=<filename>" cause the specified file
 * to also be read as a property file
 *
 */

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "globals.h"
#include "javalink.h"
#include "properties.h"

// Visual C++ specific
#ifdef _MSC_VER
    #define strncasecmp _strnicmp
#endif

#define PROPERTY_LINEBUFFER_SIZE  1024
#define MAX_PROPERTIES 256

// private variables
static char **properties;
static int propertyCount;
static int propertyArraySize;
static char *buffer;


/** 
 * Private function, may be called recursively
 */
static void readProperties0(const char *filename, int padding)
    {
    FILE *propFile;

    if (!filename || !(*filename))
        return;

    propFile = fopen(filename, "r");
    if (!propFile)
        {
        q2java_gi.dprintf("Couldn't open property file [%s]\n", filename);
        return;
        }

    while (fgets(buffer, PROPERTY_LINEBUFFER_SIZE, propFile))
        {
        char *q;
        char *p;

        // skip over leading whitespace
        p = buffer;
        while ((*p) && isspace(*p))
            p++;

        // trim trailing whitespace
        q = p + strlen(p);
        while ((q > p) && isspace(*(q-1)))
            q--;
        *q = 0;

        // ignore comments and blank lines
        if ((*p == '#') || (p == q))
            continue;

        // look for include lines
        if (!(strncasecmp(p, "include=", 8)))
            {
            // recursively call ourselves
            readProperties(p+8, padding);
            continue;
            }

        // pass lines that match "q2java_*=*" back to the
        // javalink module for special processing
        if (!strncmp(p, "q2java_", 7))
            {
            q = strchr(p, '=');
            if (q)
                {
                *q = 0;
                javalink_property(p, q+1);
                }
            continue;
            }    

        // everything else is passed to the VM as a System Property
        // (or VM option in the case of JDK 1.2)

        // ensure property array is big enough
        if (propertyCount >= propertyArraySize)
            {
            char **newproperties = q2java_gi.TagMalloc((propertyArraySize + MAX_PROPERTIES) * sizeof(char *), TAG_GAME);
            if (newproperties)
                {
                // we were able to grow the array
                memcpy(newproperties, properties, propertyArraySize * sizeof(char *));
                q2java_gi.TagFree(properties);
                properties = newproperties;
                propertyArraySize += MAX_PROPERTIES;
                }
            }

        if (propertyCount < propertyArraySize)
            {                
            properties[propertyCount] = q2java_gi.TagMalloc(strlen(p) + 1 + padding, TAG_GAME);
            strcpy(properties[propertyCount], p);
            propertyCount++;
            }
        }

    fclose(propFile);
    }


/**
 * Read the properties file for VM options
 *
 * @param filename name of properties file
 * @param padding how many extra bytes to allocate for each string
 * @return an array of pointers to char, the end of the list signaled
 *   with a null pointer.
 */
char **readProperties(const char *filename, int padding)
    {
    // clear global property variables
    propertyCount = 0;

    // allocate an array of char pointers
    properties = q2java_gi.TagMalloc(MAX_PROPERTIES * sizeof(char *), TAG_GAME);
    propertyArraySize = MAX_PROPERTIES;

    // allocate memory to read lines into
    buffer = q2java_gi.TagMalloc(PROPERTY_LINEBUFFER_SIZE, TAG_GAME);

    // fill in the q2java.home directory property
    properties[propertyCount] = q2java_gi.TagMalloc(strlen(javalink_gameDirName) + 13 + padding, TAG_GAME);
    strcpy(properties[propertyCount], "q2java.home=");
    strcat(properties[propertyCount], javalink_gameDirName);
    propertyCount++;

    // read the primary file (it may "include" others)
    readProperties0(filename, padding);

    // don't need linebuffer memory any more
    q2java_gi.TagFree(buffer);

    // mark the end of the property list
    properties[propertyCount] = 0;

    return properties;
    }


/**
 * Count the number of entries in a property list.
 *
 * @param propList an array of pointers to char, the end of the list signaled
 *   with a null pointer.
 * @return the number of strings found in the list
 */
int getPropertyCount(char **propList)
    {
    char **prop;
    int count;

    for (prop = propList, count = 0; *prop; prop++, count++)
        ;

    return count;
    }


/**
 * Free memory used to construct property list
 *
 * @param propList an array of pointers to char, the end of the list signaled
 *   with a null pointer.
 */
void freeProperties(char **propList)
    {
    char **prop;

    for (prop = propList; *prop; prop++)
        q2java_gi.TagFree(*prop);

    q2java_gi.TagFree(propList);
    }
