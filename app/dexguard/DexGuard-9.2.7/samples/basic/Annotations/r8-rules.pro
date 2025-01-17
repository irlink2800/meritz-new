# R8/D8 strips runtime-invisible annotations by default. The following
# configuration prevents that from happening.
# In turn, this makes sure that DexGuard can see the runtime-invisible
# annotations during post-processing.
-dontshrink
-dontoptimize
-dontobfuscate
