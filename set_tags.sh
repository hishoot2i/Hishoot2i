BRANCH="master"

if [ "$TRAVIS_BRANCH" = "$BRANCH" ]; then
  if [ "$TRAVIS_PULL_REQUEST" = false ]; then
  	if [ -z "$TRAVIS_TAG" ]; then
  	  echo -e "Starting to tag commit.\n"
	  git config --global user.email "travis@travis-ci.org"
	  git config --global user.name "Travis"

	  # Version tag.
	  MANIFEST="app/build/intermediates/manifests/full/release/AndroidManifest.xml"
	  APK_VERSION=$(cat $MANIFEST | grep versionName | awk '{print $1}' | sed 's/android:versionName=//g' | sed 's/"//g' )
	  echo -e "APK_VERSION=$APK_VERSION"

	  # Add tag and push to master.
	  git tag -a "v${APK_VERSION}" -f -m "Travis build $APK_VERSION pushed a tag."
	  git push origin --tags
	  git fetch origin
	  echo -e "Done magic with tags.\n"
	fi
  fi
fi