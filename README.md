# Project 4 - *ChirpNTweet*

**ChirpNTweet** is an android app that allows a user to view home and mentions timelines, view user profiles with user timelines, as well as compose and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **16** hours spent in total on Week 4 stories

## User Stories

The following **required** functionality is completed:

* [x] The app includes **all required user stories** from Week 3 Twitter Client
* [x] User can **switch between Timeline and Mention views using tabs**
  * [x] User can view their home timeline tweets.
  * [x] User can view the recent mentions of their username.
* [x] User can navigate to **view their own profile**
  * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can **click on the profile image** in any tweet to see **another user's** profile.
 * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
 * [x] Profile view includes that user's timeline
* [x] User can [infinitely paginate](http://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView) any of these timelines (home, mentions, user) by scrolling to the bottom

The following **optional** features are implemented:

* [x] User can view following / followers list through the profile
* [x] Implements robust error handling, [check if internet is available](http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity), handle error cases, network failures
* [x] When a network request is sent, user sees an [indeterminate progress indicator](http://guides.codepath.com/android/Handling-ProgressBars#progress-within-actionbar)
* [x] User can **"reply" to any tweet on their home timeline**
  * [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] User can click on a tweet to be **taken to a "detail view"** of that tweet
 * [x] User can take favorite (and unfavorite) or retweet actions on a tweet
* [x] User can **search for tweets matching a particular query** and see results
* [x] Usernames and hashtags are styled and clickable within tweets [using clickable spans](http://guides.codepath.com/android/Working-with-the-TextView#creating-clickable-styled-spans)

The following **bonus** features are implemented:

* [x] Parcelable used
* [x] Leverages the [data binding support module](http://guides.codepath.com/android/Applying-Data-Binding-for-Views) to bind data into layout templates.
* [x] On the profile screen, leverage the [CoordinatorLayout](http://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to [apply scrolling behavior](https://hackmd.io/s/SJyDOCgU) as the user scrolls through the profile timeline.
* [x] User can only view their direct messages
* [x] User can Follow/Unfollow from the Follower/Following list

The following **additional** features are implemented:

* [x] UI similiar to official twitter app
  * [x] Navigation drawer with header image
  * [x] Color pallete similiar to official twitter app. e.g. white toolbars, color pallate themed icons
* [x] Clickable spans
  * [x] When clicked on @Username profile view is shown
  * [x] When clicked on #hashtag serach view with related tweets is shown
* [x] Signout option
* [x] Retrolambda used for even handling
* [x] Twitter Key and Secret stored on a file

## Video Walkthrough on Dropbox

<a href='https://www.dropbox.com/s/bqmocm9goe78qji/Week%204%20ChirpNTweet%20VideoWalkthrough.mp4?dl=0'>Dropbox Link ....</a>

## Notes

1. Should have designed the app before implementing all features. Desiging as I added new features led to a design which is not optimum
2. Code reuse and inheritance made developing very simple, but the code could still use more OO design

## Open-source libraries used


- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide] (https://github.com/bumptech/glide) - An image loading and caching library for Android focused on smooth scrolling
- [DBFlow] (https://github.com/Raizlabs/DBFlow) - A blazing fast, powerful, and very simple ORM android database library that writes database code for you.
- [ButterKnife] (http://jakewharton.github.io/butterknife/) - Field and method binding for Android views
- [Glide Transformations] (https://github.com/wasabeef/glide-transformations) - An Android transformation library providing a variety of image transformations for Glide
- [google-gson] (https://github.com/google/gson) - A Java serialization/deserialization library that can convert Java Objects into JSON and back.

## License

    Copyright [2017] [Divya Yadav]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.