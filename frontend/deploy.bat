npm run build
cd .output
7z a ..\output.zip *
cd ..
scp output.zip burnet:/data/www/site/burnet.gs.washington.edu/html/multiomics-api