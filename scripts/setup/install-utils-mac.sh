#!/bin/bash

/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
brew install wget
brew cask install virtualbox
brew cask install docker
brew install kubectl
brew link --overwrite kubernetes-cli
brew cask install minikube
brew install git
brew cask install java
brew install maven
brew install python
brew install awscli
brew install skaffold