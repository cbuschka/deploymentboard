class IssuesStore {

  constructor() {
    this.state = {
      ok: true,
      message: '',
      issueStreams: [],
      issues: {}
    };
  }

  onIssuesLoaded({data}) {
    const {issueStreams, issues} = data;
    this.state = {issueStreams, issues};
  }

  onLoadingIssuesFailed({data}) {
    const {message} = data;
    this.state = {ok: false, message};
  }

  appendDataTo(target) {
    target.issues = {...this.state};
  }
}

export const issuesStore = new IssuesStore();
