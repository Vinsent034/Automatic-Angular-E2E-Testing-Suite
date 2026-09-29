import { Component, computed, inject, input } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Movie } from '../movie.model';
import { MovieService } from '../movie.service';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-reviews',
  imports: [ReactiveFormsModule],
  templateUrl: './reviews.component.html'
})
export class ReviewsComponent {
  private readonly fb = inject(FormBuilder);
  private readonly movieService = inject(MovieService);
  private readonly toastService = inject(ToastService);

  readonly movie = input.required<Movie>();
  readonly ratings = [1, 2, 3, 4, 5];

  readonly form = this.fb.nonNullable.group({
    author: ['', Validators.required],
    rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
    comment: ['', [Validators.required, Validators.minLength(3)]]
  });

  readonly reviews = computed(() => this.movie().reviews);

  readonly averageUserRating = computed(() => {
    const reviews = this.movie().reviews;
    if (reviews.length === 0) return 0;
    const sum = reviews.reduce((acc, r) => acc + r.rating, 0);
    return Math.round((sum / reviews.length) * 10) / 10;
  });

  showError(name: 'author' | 'comment'): boolean {
    const c = this.form.controls[name];
    return c.invalid && (c.touched || c.dirty);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const v = this.form.getRawValue();
    this.movieService.addReview(this.movie().id, {
      author: v.author.trim(),
      rating: Number(v.rating),
      comment: v.comment.trim()
    });
    this.form.reset({ author: '', rating: 5, comment: '' });
    this.toastService.success('Review added');
  }

  remove(reviewId: number): void {
    this.movieService.removeReview(this.movie().id, reviewId);
    this.toastService.show('Review removed', 'info');
  }

  stars(rating: number): string {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }
}
