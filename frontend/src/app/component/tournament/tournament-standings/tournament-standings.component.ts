import {Component, OnInit} from '@angular/core';
import {
  HorseTournamentHistoryRequest,
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

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | null = null;
  tournamentId: string | null = null;
  tournamentDetails: TournamentDetailDto | null = null;
  pointMap: Map<number, number> | null = null;

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
    this.standings = {
      id: -1,
      name: 'Unable To Load Participants',
      participants: [],
      tree: {thisParticipant: null}
    };

    if (!this.standings.tree.branches) {
      this.standings.tree.branches = []; // Initialize branches as an empty array
    }

    this.standings.name = details.name;
    this.standings.id = details.id;
    this.standings.participants = details.participants;

    const maxDepth = Math.ceil(Math.log2(this.standings.participants.length));
    this.buildEmptyTreeRecursively(0, this.standings.tree, maxDepth);
  }

  public buildEmptyTreeRecursively(depth: number, branch: TournamentStandingsTreeDto, maxDepth: number) {
    if (depth >= maxDepth) {
      return;
    }

    branch.branches = [];

    branch.branches[0] = {thisParticipant: null, branches: undefined};
    branch.branches[1] = {thisParticipant: null, branches: undefined};

    this.buildEmptyTreeRecursively(depth + 1, branch.branches[0], maxDepth);
    this.buildEmptyTreeRecursively(depth + 1, branch.branches[1], maxDepth);
    return;
  }

  public submit(form: NgForm) {
    // TODO to be implemented.
  }

  public generateFirstRound() {
    if (!this.standings) {
      return;
    }

    const startDate = this.tournamentDetails?.startDate;
    const participants = this.tournamentDetails?.participants;
    const id = this.tournamentId;

    if (!startDate || !participants || !id) {
      return;
    }

    const req: HorseTournamentHistoryRequest = {
      dateOfCurrentTournament: startDate,
      horses: participants
    };

    this.service.getRoundsReached(req, id.toString()).subscribe(
      {
        next: data => {
          this.pointMap = this.sumPoints(data);
          console.log(this.pointMap);
          console.log(data);
        },
        error: err => {
          console.log(err);
        }
      }
    )
    this.sortParticipants()
    console.log(this.standings.participants);
  }

  // TODO: ask why the map thingy screams without ts-ignore
  // TODO: ask why autocomplete isn't working even though I initialised the participants
  // TODO: ask why map is null even though it is initialised

  private sortParticipants() {
    if (this.standings === null) {
      return;
    }

    this.standings.participants.sort((a, b) => {
      if (this.pointMap === null || this.pointMap === undefined) {
        console.log("Map is null");
        return 0;
      }

      const pointsA = this.pointMap.get(a.horseId) || 0;
      const pointsB = this.pointMap.get(b.horseId) || 0;

      const pointsDiff = pointsB - pointsA;

      if (pointsDiff !== 0) {
        return pointsDiff;
      }

      return a.name.localeCompare(b.name);
    });
  }

  private sumPoints(participants: TournamentDetailParticipantDto[]) {

    let pointsMap = new Map<number, number>();

    participants.forEach(participant => {
      const id = participant.horseId;
      const roundReached = participant.roundReached;
      if (id !== null && roundReached !== undefined) {
        const points = this.calculatePoints(roundReached);
        const currentPoints = pointsMap.get(id) ?? 0;
        pointsMap.set(id, currentPoints + points);
      }
    });
    return pointsMap;
  }

  private calculatePoints(roundReached: number): number {
    switch (roundReached) {
      case 3:
        return 5;
      case 2:
        return 3;
      case 1:
        return 1;
      default:
        return 0;
    }
  }

  private populateLeaves() {

  }

  // TODO: add entry numbers to the participants based on the pointsMap
  // TODO: add update method to initialisation of participants in backend and frontend
  // TODO: implement DFS with counter to initialise participants.
  private populateLeavesRecursively(depth: number, branch: TournamentStandingsTreeDto, maxDepth: number) {
    if (branch.branches === undefined) {
      this.notification.error("An error occurred while generating the initial rounds.");
      return;
    }

    if (depth >= maxDepth) {
      // @ts-ignore
      branch.thisParticipant = this.pointMap
      return;
    }

    this.buildEmptyTreeRecursively(depth + 1, branch.branches[0], maxDepth);
    this.buildEmptyTreeRecursively(depth + 1, branch.branches[1], maxDepth);
    return;
  }
}
