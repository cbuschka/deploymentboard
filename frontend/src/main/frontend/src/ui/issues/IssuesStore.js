class IssuesStore {

  constructor() {
    this.state = {
      ok: true,
      message: '',
      systems: [],
      issues: {}
    };
  }

  onIssuesLoaded({data}) {
    const {systems, issues} = data;
    this.state = {systems, issues};
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
