#Make sure on master
gru

#Commit and push up changes

#Bump version to next RELEASE version in:
  README.md

#Push version change upto repo & then release
git commit -a -m "Preparing for release"
git push origin HEAD

sbt release

#goto Sonatype and stage the release
https://oss.sonatype.org/#stagingRepositories
select the bizcgta-XXXXX item at the top
press close
press release