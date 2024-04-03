import {Component, OnInit} from '@angular/core';
import {
  TournamentDetailDto,
  TournamentDetailParticipantDto,
  TournamentStandingsDto,
  TournamentStandingsTreeDto
} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ActivatedRoute} from "@angular/router";
import {NgForm} from "@angular/forms";
import {Location} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {ErrorFormatterService} from "../../../service/error-formatter.service";
import {TournamentStandingsBranchComponent} from "./tournament-standings-branch/tournament-standings-branch.component";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | undefined;
  tournamentId: string | null = null;
  tournamentDetails: TournamentDetailDto | undefined;

  public constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location,
  ) {
  }

  public ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.tournamentId = params.get('id');
      if (this.tournamentId) {
        this.service.getById(this.tournamentId).subscribe({
          next: data => {
            this.tournamentDetails = data;
            this.buildEmptyTree(data);
          },
          error: error => {
            console.error('Error fetching horse details', error);
          }
        });
      }
    });
  }

  public buildEmptyTree(details: TournamentDetailDto) {
    this.standings = { id: -1, name: 'Unable To Load Participants', participants: [], tree: {
        thisParticipant: null
      } };

    if (!this.standings.tree.branches) {
      this.standings.tree = { thisParticipant: null, branches: [] }; // Initialize branches as an empty array
    }

    this.standings.name = details.name;
    this.standings.id = details.id;
    this.standings.participants = details.participants;

    this.buildEmptyTreeRecursively(0, this.standings.tree);
  }

  public buildEmptyTreeRecursively(depth: number, branch: TournamentStandingsTreeDto) {
    if (depth > 2) {
      return;
    }

    branch.branches = [];

    branch.branches[0] = { thisParticipant: null, branches: undefined };
    branch.branches[1] = { thisParticipant: null, branches: undefined };

    this.buildEmptyTreeRecursively(depth + 1, branch.branches[0]);
    this.buildEmptyTreeRecursively(depth + 1, branch.branches[1]);
  }

  public submit(form: NgForm) {
    // TODO to be implemented.
  }

  public generateFirstRound() {
    if (!this.standings)
      return;
    // TODO implement
  }

}
