import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Location} from "@angular/common";
import {
  HorseTournamentHistoryRequest,
  TournamentDetailParticipantDto,
  TournamentStandingsDto,
  TournamentStandingsTreeDto
} from "../../../dto/tournament";
import {TournamentService} from "../../../service/tournament.service";
import {ErrorFormatterService} from "../../../service/error-formatter.service";

@Component({
  selector: 'app-tournament-standings',
  templateUrl: './tournament-standings.component.html',
  styleUrls: ['./tournament-standings.component.scss']
})
export class TournamentStandingsComponent implements OnInit {
  standings: TournamentStandingsDto | null = null;
  pointMap: Map<number, number> = new Map<number, number>();
  entryMap: Map<number, TournamentDetailParticipantDto> = new Map<number, TournamentDetailParticipantDto>();
  participantCounter: number = 0;

  constructor(
    private service: TournamentService,
    private errorFormatter: ErrorFormatterService,
    private route: ActivatedRoute,
    private notification: ToastrService,
    private location: Location
  ) {
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const tournamentId = params.get('id');
      if (tournamentId) {
        this.service.getById(tournamentId).subscribe({
          next: data => {
            this.standings = data;
            this.standings.startDate = new Date(data.startDate);
            this.fillEntryMap();
            console.log(this.standings.tree);
          },
          error: err => {
            const errorMessage = this.errorFormatter.logError(err);
            this.notification.error("Failure", errorMessage);
          }
        });
      }
    });
  }

  submit(form: NgForm) {
    this.updateTree();
    if (this.standings === null) {
      this.notification.warning("There's nothing to submit.");
      return;
    }
    console.log(this.standings.tree);

    this.service.update(this.standings, this.standings.id.toString()).subscribe({
      next: data => {
        this.standings = data;
        this.notification.success("Update successful.");
      },
      error: (err) => {
        this.notification.error(this.errorFormatter.format(err), "Failed To Update", {
          enableHtml: true,
          timeOut: 10000,
        });
        this.ngOnInit();
      }
    });
  }

  generateFirstRound() {
    const hasNonZero = Array.from(this.entryMap.values()).some(participant => participant.entryNumber !== undefined && participant.entryNumber !== 0);
    if (hasNonZero || !this.standings) {
      this.notification.error("Participants have already been assigned");
      return;
    }

    // If the standings have not yet been saved to the database then on click remove all horses
    this.ngOnInit();

    const req: HorseTournamentHistoryRequest = {
      dateOfCurrentTournament: new Date(this.standings.startDate),
      horses: this.standings.participants
    };

    this.service.getRoundsReached(req, this.standings.id.toString()).subscribe(
      {
        next: data => {
          this.sumPoints(data);
          this.sortAndMapParticipants();
          this.populateLeaves();
        },
        error: error => {
          this.notification.error(this.errorFormatter.format(error), "Could Not Generate First Round", {
            enableHtml: true,
            timeOut: 10000,
          });
        }
      }
    )
  }

  public fillEntryMap() {
    this.standings?.participants
      .forEach(participant => {
        if (participant.entryNumber !== undefined && participant.entryNumber !== 0)
          this.entryMap.set(participant.entryNumber, participant);
      });
  }

  sortAndMapParticipants() {
    if (!this.standings) {
      return;
    }

    this.standings.participants.sort((a, b) => {
      const pointsDiff = (this.pointMap.get(b.horseId) || 0) - (this.pointMap.get(a.horseId) || 0);
      return pointsDiff !== 0 ? pointsDiff : a.name.localeCompare(b.name);
    });
  }

  sumPoints(participants: TournamentDetailParticipantDto[]) {
    participants.forEach(participant => {
      if (participant.horseId !== null && participant.roundReached !== undefined) {
        const points = this.calculatePoints(participant.roundReached);
        const currentPoints = this.pointMap.get(participant.horseId) ?? 0;
        this.pointMap.set(participant.horseId, currentPoints + points);
      }
    });
  }

  calculatePoints(roundReached: number): number {
    switch (roundReached) {
      case 1:
        return 5;
      case 2:
        return 3;
      case 3:
        return 1;
      default:
        return 0;
    }
  }

  populateLeaves() {
    const tree = this.standings?.tree;
    if (!tree || this.standings === null) {
      return;
    }

    this.participantCounter = 0;
    const maxDepth = Math.ceil(Math.log2(this.standings.participants.length)) + 1;
    this.populateLeavesRecursively(1, tree, maxDepth);
  }

  populateLeavesRecursively(depth: number, branch: TournamentStandingsTreeDto, maxDepth: number) {
    if (depth >= maxDepth) {
      const participant = this.standings?.participants[this.participantCounter];
      if (participant) {
        this.participantCounter += 1;
        branch.thisParticipant = participant;
      }
      return;
    }

    if (branch.branches === undefined) {
      this.notification.error("An error occurred while generating the initial rounds.");
      return;
    }

    this.populateLeavesRecursively(depth + 1, branch.branches[0], maxDepth);
    this.populateLeavesRecursively(depth + 1, branch.branches[1], maxDepth);
  }

  public updateTree() {
    const tree = this.standings?.tree;
    if (!tree || this.standings === null) {
      return;
    }

    this.participantCounter = 0;
    const maxDepth = Math.ceil(Math.log2(this.standings.participants.length)) + 1;
    this.updateTreeRecursively(1, tree, maxDepth);
  }

  public updateTreeRecursively(depth: number, branch: TournamentStandingsTreeDto, maxDepth: number) {
    const participant = branch.thisParticipant;
    if (depth >= maxDepth) {
      this.participantCounter += 1;
      if (participant !== null) {
        participant.entryNumber = this.participantCounter;
        const entry = this.entryMap.get(participant.entryNumber);
        participant.roundReached = entry && entry.roundReached ? entry.roundReached : depth;
        this.entryMap.set(participant.entryNumber, participant);
      }
      return;
    }

    if (branch.branches === undefined) {
      this.notification.error("An error occurred while updating the tree.");
      return;
    }

    if (participant !== null && participant.entryNumber !== undefined) {
      const entry = this.entryMap.get(participant.entryNumber);
      if (entry && entry.roundReached) {
        this.entryMap.set(participant.entryNumber, participant);
        participant.roundReached = Math.min(depth, entry.roundReached);
      }
    }

    this.updateTreeRecursively(depth + 1, branch.branches[0], maxDepth);
    this.updateTreeRecursively(depth + 1, branch.branches[1], maxDepth);
  }
}
