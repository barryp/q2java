#ifndef _Included_properties
#define _Included_properties

char **readProperties(const char *filename, int padding);
void freeProperties(char **propList);
int getPropertyCount(char **propList);

#endif
