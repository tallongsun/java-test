dir=`ls`
for i in $dir
do
  echo "git rm -r $i/target/"
  git rm -r $i/target/
done
