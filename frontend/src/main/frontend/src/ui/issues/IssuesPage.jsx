import React from 'react';
import {dispatcher} from "@cbuschka/flux";
import {issuesStore} from "./IssuesStore";
import {loadIssues} from "./LoadIssuesAction";
import {IssuesMatrix} from "./IssuesMatrix";
import './IssuesPage.css';

export class IssuesPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {systems: []}
    }

    componentDidMount() {
        dispatcher.addHandler(issuesStore);
        dispatcher.subscribe(this._onChange);
        this._reloadIssues();
    }

    componentWillUnmount() {
        window.clearTimeout(this.reloadTimer);
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(issuesStore);
    }

    _reloadIssues = () => {
        loadIssues()
            .finally(() => {
                this.reloadTimer = window.setTimeout(this._reloadIssues, 5000);
            });
    }

    _onChange = ({data}) => {
        const {issues: {issueStreams}} = data;
        this.setState({issueStreams});
    };


    render() {
        const {issueStreams} = this.state;

        return <div className="IssuesPage">
            <IssuesMatrix issueStreams={issueStreams}/>
        </div>;
    }
}
