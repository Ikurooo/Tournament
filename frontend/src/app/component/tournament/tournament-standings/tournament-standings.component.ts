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
  tournamentDetails: TournamentDetailDto | null = null;
  pointMap: Map<number, number> = new Map<number, number>();
  entryMap: Map<number, TournamentDetailParticipantDto> = new Map<number, TournamentDetailParticipantDto>();
  participantCounter: number = 0;
  participantCount: number = 0;

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
            this.participantCount = this.standings.participants.length;
            this.fillEntryMap();
            console.log(this.entryMap);

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
    console.log(this.entryMap);

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
    if (!this.standings) {
      return;
    }

    const hasNonZero = Array.from(this.entryMap.values()).some(participant => participant.entryNumber !== undefined && participant.entryNumber !== 0);
    if (hasNonZero) {
      return;
    }

    const startDate = new Date(this.standings.startDate);
    const participants = this.standings.participants;
    const id = this.standings.id;

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
      const pointsA = this.pointMap.get(a.horseId) || 0;
      const pointsB = this.pointMap.get(b.horseId) || 0;
      const pointsDiff = pointsB - pointsA;

      if (pointsDiff !== 0) {
        return pointsDiff;
      }

      return a.name.localeCompare(b.name);
    });

    this.standings.participants.forEach((participant, index) => {
      participant.entryNumber = index + 1;
    });
  }

  sumPoints(participants: TournamentDetailParticipantDto[]) {
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

    const maxDepth = Math.ceil(Math.log2(this.standings.participants.length)) + 1;
    this.populateLeavesRecursively(1, tree, maxDepth);
  }

  populateLeavesRecursively(depth: number, branch: TournamentStandingsTreeDto, maxDepth: number) {
    if (depth >= maxDepth) {
      const participant = this.standings?.participants[this.participantCounter % this.participantCount];

      if (!participant) {
        return;
      }

      branch.thisParticipant = participant;
      this.participantCounter += 1;
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
