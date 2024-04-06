import {Component, OnInit} from '@angular/core';
import {NgForm} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {Location} from "@angular/common";
import {
  HorseTournamentHistoryRequest,
  TournamentDetailDto,
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
  tournamentId: string | null = null;
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
      this.tournamentId = params.get('id');
      if (this.tournamentId) {
        this.service.getById(this.tournamentId).subscribe({
          next: data => {
            this.standings = data;
            this.standings.startDate = new Date(data.startDate);
            this.fillEntryMap();
          },
          error: error => {
            this.notification.error('Error fetching horse details', error);
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
      this.notification.success("Update sent.");
    },
    error: (err) => {
      console.error("Error updating standings:", err);
      this.notification.error("Failed to update standings.");
    }
  });
  }

  generateFirstRound() {
    const hasNonZero = Array.from(this.entryMap.values()).some(participant => participant.entryNumber !== undefined && participant.entryNumber !== 0);
    if (hasNonZero || !this.standings) {
      this.notification.error("Participants have already been assigned");
      return;
    }

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
        error: err => {
          this.notification.error("Error fetching rounds reached");
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
        participant.roundReached = depth;
        this.entryMap.set(participant.entryNumber, participant);
      }
      return;
    }

    if (branch.branches === undefined) {
      this.notification.error("An error occurred while updating the tree.");
      return;
    }

    this.updateTreeRecursively(depth + 1, branch.branches[0], maxDepth);
    this.updateTreeRecursively(depth + 1, branch.branches[1], maxDepth);

    if (participant !== null && participant.entryNumber !== undefined) {
      const entry = this.entryMap.get(participant.entryNumber);
      if (entry && entry.roundReached) {
        participant.roundReached = Math.min(depth, entry.roundReached);
      }
      this.entryMap.set(participant.entryNumber, participant);
    }
  }
}
