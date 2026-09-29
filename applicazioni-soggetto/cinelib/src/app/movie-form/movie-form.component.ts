import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovieService } from '../movie.service';
import { ToastService } from '../toast.service';
import { CastMember } from '../movie.model';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-movie-form',
  imports: [ReactiveFormsModule, PanelComponent],
  templateUrl: './movie-form.component.html'
})
export class MovieFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly movieService = inject(MovieService);
  private readonly toastService = inject(ToastService);

  private editId: number | null = null;
  readonly isEdit: boolean;
  readonly genres = ['Adventure', 'Sci-Fi', 'Drama', 'Action', 'Romance', 'Thriller'];

  readonly form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    year: [2024, [Validators.required, Validators.min(1900), Validators.max(2100)]],
    genre: ['Drama', Validators.required],
    rating: [4, [Validators.required, Validators.min(0), Validators.max(5)]],
    runtime: [100, [Validators.required, Validators.min(1), Validators.max(600)]],
    director: ['', Validators.required],
    synopsis: [''],
    cast: this.fb.array<ReturnType<MovieFormComponent['castGroup']>>([])
  });

  get cast(): FormArray {
    return this.form.controls.cast;
  }

  constructor() {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!idParam;
    if (idParam) {
      const movie = this.movieService.getById(Number(idParam));
      if (movie) {
        this.editId = movie.id;
        this.form.patchValue({
          title: movie.title,
          year: movie.year,
          genre: movie.genre,
          rating: movie.rating,
          runtime: movie.runtime,
          director: movie.director,
          synopsis: movie.synopsis
        });
        movie.cast.forEach((c) => this.cast.push(this.castGroup(c)));
      }
    }
  }

  private castGroup(member?: CastMember) {
    return this.fb.nonNullable.group({
      name: [member?.name ?? '', Validators.required],
      role: [member?.role ?? '', Validators.required]
    });
  }

  addCast(): void {
    this.cast.push(this.castGroup());
  }

  removeCast(index: number): void {
    this.cast.removeAt(index);
  }

  /** True when the control should show its error (invalid and interacted with). */
  showError(name: 'title' | 'year' | 'genre' | 'rating' | 'runtime' | 'director'): boolean {
    const c = this.form.controls[name];
    return c.invalid && (c.touched || c.dirty);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    const data = {
      title: v.title.trim(),
      year: Number(v.year),
      genre: v.genre,
      rating: Number(v.rating),
      runtime: Number(v.runtime),
      director: v.director.trim(),
      synopsis: v.synopsis.trim(),
      cast: v.cast.map((c) => ({ name: c.name.trim(), role: c.role.trim() }))
    };
    if (this.isEdit && this.editId != null) {
      this.movieService.update(this.editId, data);
      this.toastService.success(`Updated “${data.title}”`);
      this.router.navigate(['/movie', this.editId]);
    } else {
      const created = this.movieService.add(data);
      this.toastService.success(`Created “${data.title}”`);
      this.router.navigate(['/movie', created.id]);
    }
  }

  cancel(): void {
    if (this.isEdit && this.editId != null) {
      this.router.navigate(['/movie', this.editId]);
    } else {
      this.router.navigate(['/']);
    }
  }
}
