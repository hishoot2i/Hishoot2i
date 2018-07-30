BRANCH="master"

if [ "$TRAVIS_BRANCH" = "$BRANCH" ]; then
  if [ "$TRAVIS_PULL_REQUEST" = false ]; then
  	if [ -z "$TRAVIS_TAG" ]; then
  	  echo -e "Starting to tag commit.\n"
	  git config --global user.email "travis@travis-ci.org"
	  git config --global user.name "Travis"

	  # Version name for tag ???
      VER="VERSION"
      APK_VERSION=$(cat $VER | awk '{print $1}')
      echo -e "APK_VERSION is $APK_VERSION"

	  # Add tag and push to master.
	  git tag -a "v${APK_VERSION}" -f -m "Travis build $APK_VERSION pushed a tag."
	  git push origin --tags
	  git fetch origin
	  echo -e "Done magic with tags.\n"
	fi
  fi
fi